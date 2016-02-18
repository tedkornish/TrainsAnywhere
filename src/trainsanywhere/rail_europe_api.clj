(ns trainsanywhere.rail-europe-api
  (:require [cemerick.url :refer [url]]
            [clojure.data.json :as json]
            [clj-http.client :as client]
            [camel-snake-kebab.core :refer [->snake_case_keyword]]
            [korma.core :as core]
            [trainsanywhere.models :as models]
            [clojure.string :as string]
            [clojure.set :refer [rename-keys]]))

(defn url-for-station-page
  "Assemble the URL for fetching a page of stations from the Rail Europe API."
  [n]
  (-> (url "https://www2.raileurope.com/us/shopping/popular-locations.htm?")
      (assoc :query {:batchSize 20 :batchNr n})
      str))

(defn- capitalize-words 
  "Capitalize every word in a string."
  [s]
  (->> (string/split (str s) #"\b") 
       (map string/capitalize)
       string/join))

(defn- call-function-for-keys
  "Given a sequence of maps, a sequence of keys, and a unary function which
  return a single value, call-function-for-keys will call the given function `f`
  for the values of each of the `keys` for each map in `maps`."
  [maps keys f] 
  (loop [current-maps maps
         remaining-keys keys]
    (let [current-key (first remaining-keys)
          update-fn #(update-in % [current-key] f)]
      (if (empty? remaining-keys) current-maps
        (recur
          (map update-fn current-maps)
          (rest remaining-keys))))))

(defn- swap-map
  "swap-map is map, but with arguments reversed. Useful when passing a list
  through the -> macro."
  [seq f] 
  (map f seq))

(defn fetch-stations-page
  "Fetch a single page of stations from the Rail Europe API. Returns nil if no
  page to fetch."
  [n] 
  (println (str "Fetching page " n))
  (-> n
      url-for-station-page
      client/get
      (get :body)
      (json/read-str :key-fn ->snake_case_keyword)
      (get :list)
      (call-function-for-keys [:id :latitude :longitude] read-string)
      (call-function-for-keys [:name :native_name] capitalize-words)
      (swap-map #(rename-keys % {:id :station_id}))))

(defn fetch-all-stations
  "Fetch all stations before returning them in a list. Does not write to DB."
  [] 
  (loop [stations []
         current-page 1]
    (let [page (fetch-stations-page current-page)]
      (if (empty? page)
        stations
        (recur (concat stations page) (inc current-page))))))

(defn fetch-and-insert-all-stations []
  (let [stations (fetch-all-stations)]
    (println "Inserting...")
    (core/insert models/stations (core/values stations))))
