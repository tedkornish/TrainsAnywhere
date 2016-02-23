(ns trainsanywhere.scheduler.repl
  (:require [taoensso.carmine.message-queue :as car-mq]
            [taoensso.carmine :as car]
            [clojure.data.json :as json]))

(defn enqueue-raw 
  "Day is formatted like 1/22/2016."
  [source target day]
  (let [message {:source-name source :target-name target :day-str day}]
    (car/wcar {} (car-mq/enqueue "to-scrape" (json/write-str message)))))
