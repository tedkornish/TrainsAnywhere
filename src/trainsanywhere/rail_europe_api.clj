(ns trainsanywhere.rail-europe-api
  (:require [cemerick.url :refer [url]]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [camel-snake-kebab.core :refer [->snake_case_keyword]]
            [korma.core :as core]
            [trainsanywhere.models :as models]))

(defn- url-for-station-page [n]
  "Assemble the URL for fetching a page of stations from the Rail Europe API."
  (-> (url "https://www2.raileurope.com/us/shopping/popular-locations.htm?")
      (assoc :query {:batchSize 20 :batchNr n})
      str))

(defn- fetch-stations-page [n]
  "Fetch a single page of stations from the Rail Europe API. Returns nil if no
  page to fetch."
  (-> n
      url-for-station-page
      client/get
      (get :body)
      (json/read-str :key-fn ->snake_case_keyword)
      (get :list)))

(defn- fetch-all-stations []
  "Fetch all stations before returning them in a list. Does not write to DB."
  (loop [stations []
         current-page 1]
    (let [page (fetch-stations-page current-page)]
      (if (nil? page)
        stations
        (recur (concat stations page) (inc current-page))))))

(defn fetch-and-insert-all-stations []
  (let [stations (fetch-all-stations)])
    (core/insert models/stations (core.values stations)))
