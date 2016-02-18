(ns trainsanywhere.persistence.core
  (:require [taoensso.carmine.message-queue :as car-mq]
            [clojure.data.json :as json]
            [trainsanywhere.persistence.data :as data]))

(defn run-persistence-worker [{message-id :mid message-raw :message}]
  (println (str "Processing message " message-id "..."))
  (let [message (json/read-str message-raw :key-fn keyword)]
    (data/persist-nested-models (data/scraped-data-to-nested-models message))
    {:status :success}))

(defn -main []
  (car-mq/worker {} "to-write" {:handler run-persistence-worker}))
