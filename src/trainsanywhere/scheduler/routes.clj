(ns trainsanywhere.scheduler.routes
  (:require [trainsanywhere.scheduler.config :as config]
            [korma.core :refer [select order limit insert values dry-run]]
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
