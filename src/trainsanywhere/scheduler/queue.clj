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
                 :target-name (:destination_station_name route)}]
    (car/wcar {} (car-mq/enqueue "to-scrape" (json/write-str message)))))

(defn top-pending-routes []
  (select routes-pending-fetches (limit 10000)))

(defn update-to-fetching [route]
  (update routes
          (set-fields {:fetch_status "fetching"
                       :last_fetch_queued_at (c/to-sql-date (t/now))})
          (where {:id (:id route)})))

(defn enqueue-top-pending-routes []
  (doseq [r (top-pending-routes)]
    (update-to-fetching r)
    (enqueue r)))
