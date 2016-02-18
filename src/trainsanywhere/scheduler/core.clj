(ns trainsanywhere.scheduler.core
  (:require [clojure.data.json :as json]
            [taoensso.carmine.message-queue :as car-mq]
            [taoensso.carmine :as car]
            [trainsanywhere.models :as models])
  (:use korma.core))

(def num-stations
  (let [n (Integer/parseInt (System/getEnv "TA_NUM_STATIONS"))]
    (if (< 2 n)
      (throw (Exception. "can't use less than 2 stations in scheduler"))
      n)))

(defn enqueue [source target day]
  (let [message {:source source :target target :day day}]
    (car/wcar {} (car-mq/enqueue "to-scrape" (json/write-str message)))))
