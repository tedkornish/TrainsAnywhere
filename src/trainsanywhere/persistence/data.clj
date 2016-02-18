(ns trainsanywhere.persistence.data
  (:require [schema.core :as s]
            [korma.core :refer [insert values]]
            [trainsanywhere.models :refer [trips hops]])) 

;;;; Utilities for formatting and persisting data scraped from the Rail Europe
;;;; website.

(def ScrapedData
  "A schema for data scraped from the Rail Europe website."
  {:trips [{:prices {:economy s/Str
                     :comfort s/Str
                     :premiere s/Str}
            :hops [{:departure-time s/Str
                    :departure-station s/Str
                    :arrival-time s/Str
                    :arrival-station s/Str
                    :duration s/Int}]}]
   :original {:source-id s/Int
              :target-id s/Int
              :day-str s/Str
              :source-name s/Str
              :target-name s/Str
              :route-id s/Int}})

(def NestedModels
  [{:trip {:price_economy_dollars s/Str
           :price_comfort_dollars s/Str
           :price_premier_dollars s/Str
           :route_id s/Int}
    :hops [{:origin_station_name s/Str
            :destination_station_name s/Str
            :departure_time_string s/Str
            :arrival_time_string s/Str
            :duration_minutes s/Int}]}])

(defn sanitize-scraped-hop [hop orig]
  {:origin_station_name (:departure-station hop)
   :destination_station_name (:arrival-station hop)
   :departure_time_string (:departure-time hop)
   :arrival_time_string (:arrival-time hop)
   :duration_minutes (:duration hop) })

(defn sanitize-scraped-trip [trip orig]
  (let [prices (:prices trip)]
    {:trip {:price_economy_dollars (:economy prices)
            :price_comfort_dollars (:comfort prices)
            :price_premier_dollars (:premiere prices)
            :route_id (:route-id orig)}
     :hops (map #(sanitize-scraped-hop % orig) (:hops trip))}))

(defn scraped-data-to-nested-models
  "Turns data satisfying ScrapedData (above) into data satisfying NestedModels
  for insertion into the database."
  [scraped-data]
  ;{:pre [(s/validate ScrapedData scraped-data)]
   ;:post [(s/validate NestedModels %)]}
  (map #(sanitize-scraped-trip % (:original scraped-data))
       (:trips scraped-data)))

(defn persist-nested-models [nested-models]
  ;{:pre [(s/validate NestedModels nested-models)]}
  (doseq [trip nested-models] ;; insert each trip
    (let [inserted (insert trips (values (:trip trip)))]
      (doseq [child (:hops trip)] ;; grab the trip_id and insert all hops
        (insert hops (values (merge child {:trip_id (:id inserted)})))))))
