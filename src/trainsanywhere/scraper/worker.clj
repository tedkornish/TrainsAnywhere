(ns trainsanywhere.scraper.worker
  (:require [taoensso.carmine.message-queue :as car-mq]
            [clojure.data.json :as json]
            [clj-webdriver.core :as wd]
            [trainsanywhere.scraper.web :as web]))

(defn run-scraper-worker [{message-id :mid message-raw :message}]
  ;; deserialize from JSON
  ;; make a driver
  ;; grab trip info
  ;; serialize to json and print
  ;; kill driver
  
  (println (str "Processing message " message-id "..."))
  (let [message (json/read-str message-raw :key-fn keyword)
        driver (wd/new-driver {:browser :chrome})
        trip-info (web/fetch-route-info
                    driver
                    (:source message)
                    (:target message)
                    (:day message))]
    (println (json/write-str trip-info))
    (wd/quit driver)
    {:status :success}))

(def main
  (car-mq/worker {} "to-scrape" {:handler run-scraper-worker}))
