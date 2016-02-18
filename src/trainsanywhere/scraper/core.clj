(ns trainsanywhere.scraper.core
  (:require [taoensso.carmine.message-queue :as car-mq]
            [clojure.data.json :as json]
            [clj-webdriver.core :as wd]
            [trainsanywhere.scraper.web :as web]
            [trainsanywhere.scraper.data :as data]))

(defn run-scraper-worker [{message-id :mid message-raw :message}]
  (println (str "Processing message " message-id "..."))
  (let [message (json/read-str message-raw :key-fn keyword) ;; deserialize
        driver (wd/new-driver {:browser :phantomjs}) ;; make driver
        trip-info (web/fetch-route-info driver message)]
    (data/persist-nested-models (data/scraped-data-to-nested-models trip-info))
    (wd/quit driver) ;; kill driver
    {:status :success}))

(defn -main []
  (car-mq/worker {} "to-scrape" {:handler run-scraper-worker}))
