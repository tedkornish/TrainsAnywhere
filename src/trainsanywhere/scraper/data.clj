(ns trainsanywhere.scraper.data
  (:require [schema.core :as s]))

;;;; This file contains utilities for turning the structured data that we scrape
;;;; from the website into relational models for persistence in Postgres.

(def ScrapedData
  "A schema for data scraped from the Rail Europe website."
  :trips [{:prices {:economy s/Str
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
             :route-id s/Int})

(def NestedModels
  [{:trip {:price_economy_dollars s/Str
           :price_comfort_dollars s/Str
           :price_premier_dollars s/Str
           :route_id s/Int}
    :hops [{:origin_station_name s/Str
            :destination_station_name s/Str
            :departure_time s/Str
            :arrival_time s/Str
            :duration_minutes s/Int}]}])

(def time-from
  "day-str is something like 2/16/2016. Time-str is something like 11:25AM."
  [day-str time-str]
  (str day-str " " time-str))

(def sanitize-scraped-hop [hop orig]
  {:origin_station_name (:departure-station hop)
   :destination_station_name (:arrival-station hop)
   :departure_time (time-from (:day-str orig) (:departure-time hop))
   :arrival_time (time-from (:day-str orig) (:arrival-time hop))
   :duration_minutes (:duration hop) })

(def sanitize-scraped-trip [trip orig]
  (let [prices (:prices trip)]
    {:trip {:price_economy_dollars (:economy prices)
            :price_comfort_dollars (:comfort prices)
            :price_premier_dollars (:premiere prices)
            :route_id (:route-id orig)}
     :hops (map #(sanitize-scraped-hop % orig) (:hops trip))}))

(def scraped-data-to-nested-models
  "Turns data satisfying ScrapedData (above) into data satisfying NestedModels
  for insertion into the database."
  [scraped-data]
  (map #(sanitize-scraped-trip % (:original scraped-data))
       (:trips scraped-data)))
