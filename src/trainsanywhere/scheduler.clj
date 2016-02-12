(ns trainsanywhere.scheduler
  (:require [clojure.data.json :as json]
            [taoensso.carmine.message-queue :as car-mq]
            [taoensso.carmine :as car]))

(defn enqueue [source target day]
  (let [message {:source source :target target :day day}]
    (car/wcar {} (car-mq/enqueue "to-scrape" (json/write-str message)))))
