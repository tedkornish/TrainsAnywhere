(ns trainsanywhere.scraper.core
  (:require [taoensso.carmine.message-queue :as car-mq]
            [clojure.data.json :as json]
            [clj-webdriver.core :as wd]
            [trainsanywhere.scraper.web :as web]))

(defn run-scraper-worker [{message-id :mid message-raw :message}]
  (println (str "Processing message " message-id "..."))
  (let [message (json/read-str message-raw :key-fn keyword) ;; deserialize
        driver (wd/new-driver {:browser :chrome}) ;; make driver
        trip-info (web/fetch-route-info driver message)]
    (println (json/write-str trip-info)) ;; print
    (wd/quit driver) ;; kill driver
    {:status :success}))

(def main
  (car-mq/worker {} "to-scrape" {:handler run-scraper-worker}))
