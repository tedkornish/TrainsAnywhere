(ns trainsanywhere.scheduler.routes
  (:require [trainsanywhere.scheduler.config :as config]
            [korma.core :refer [select order limit insert values aggregate]]
            [trainsanywhere.models :refer [stations routes]]
            [clj-time.core :as t]
            [clj-time.periodic :as p]
            [clj-time.coerce :as c]))

(defn get-popular-station-tuples
  "Get the most popular stations, then create a list of tuples between them."
  []
  (let [stations (select stations (order :id :ASC) (limit config/num-stations))]
    (for [s1 stations s2 stations :when (not= s1 s2)]
      [s1 s2])))

(defn next-n-days
  "The next n days, midnight, starting with midnight today UTC."
  [n]
  (take n (p/periodic-seq (t/today-at 0 0) (t/days 1))))

(defn generate-routes-for-next-n-days [n]
  (for [routes (get-popular-station-tuples)
        days (next-n-days n)]
    {:origin_station_id (:id (first routes))
     :destination_station_id (:id (last routes))
     :date (c/to-sql-date days)}))

(defn insert-routes []
  (let [upcoming-routes (generate-routes-for-next-n-days 120)]
    (println "Inserting...")
    (doseq [r upcoming-routes]
      (try
        (insert routes (values r))
        (catch Exception e
          (if-not
            (.contains (str e) "duplicate")
            (throw (Exception. e))))))))

(defn routes-task-handler
  "The cronj handler for inserting routes periodically."
  [t opts]
  (insert-routes))

(defn num-routes []
  (-> (select routes (aggregate (count :*) :cnt))
      (first)
      (:cnt)))

(defn no-routes?
  "Returns true if 0 routes in the routes table."
  []
  (== 0 (num-routes)))

(def routes-task
  "The cronj task which runs every Sunday. We'll also manually insert routes on
  startup if there are no routes."
  {:id "routes-task"
   :handler routes-task-handler
   :schedule "0 0 0 * * 0" ;; run every Sunday at midnight
   :pre-hook (fn [dt opts]
               (println "Starting to insert routes..."))
   :post-hook (fn [dt opts]
                (let [num-routes (num-routes)]
                  (println
                    "Finished inserting routes. Total number of routes:"
                    num-routes)))})
