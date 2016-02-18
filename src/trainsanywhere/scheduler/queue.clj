(ns trainsanywhere.scheduler.queue
  (:require [korma.core :refer [select limit update set-fields where]]
            [trainsanywhere.models :refer [routes-pending-fetches routes]]
            [taoensso.carmine.message-queue :as car-mq]
            [taoensso.carmine :as car]
            [clojure.data.json :as json]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-time.core :as t]))

(def queue-date-formatter (f/formatter "M/d/yyyy"))

(defn enqueue [route]
  (let [message {:source-id (:origin_station_id route)
                 :target-id (:destination_station_id route)
                 :day-str (->> (:date route)
                               (c/from-sql-date)
                               (f/unparse queue-date-formatter))
                 :source-name (:origin_station_name route)
                 :target-name (:destination_station_name route)
                 :route-id (:id route)}]
    (car/wcar {} (car-mq/enqueue "to-scrape" (json/write-str message)))))

(defn top-pending-routes [n]
  (select routes-pending-fetches (limit n)))

(defn update-to-fetching [route]
  (update routes
          (set-fields {:fetch_status "fetching"
                       :last_fetch_queued_at (c/to-sql-date (t/now))})
          (where {:id (:id route)})))

(defn num-pending-items []
  (count (:messages (car-mq/queue-status {} "to-scrape"))))

(defn enqueue-top-pending-routes [n]
  (doseq [r (top-pending-routes n)]
    (update-to-fetching r)
    (enqueue r)))

(defn queue-task-handler
  "The cronj handler for queueing pending routes."
  [t opts]
  (if (> 500 (num-pending-items))
    (println "Queueing routes for fetching...")
    (enqueue-top-pending-routes 500)))

(def queue-task
  "The cronj task which runs every minute."
  {:id "queue-task"
   :handler queue-task-handler
   :schedule "* /1 * * * *"})
