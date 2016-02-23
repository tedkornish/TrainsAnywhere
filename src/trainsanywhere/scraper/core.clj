(ns trainsanywhere.scraper.core
  (:require [taoensso.carmine.message-queue :as car-mq]
            [taoensso.carmine :as car]
            [clojure.data.json :as json]
            [clj-webdriver.core :as wd]
            [trainsanywhere.scraper.web :as web]))

(def driver (wd/new-driver {:browser :chrome})) ;; make driver

(defn enqueue [scraped-data]
  (car/wcar {} (car-mq/enqueue "to-write" (json/write-str scraped-data))))

(defn run-scraper-worker [{message-id :mid message-raw :message}]
  (println (str "Processing message " message-id "..."))
  (let [message (json/read-str message-raw :key-fn keyword) ;; deserialize
        trip-info (web/fetch-route-info driver message)]
    (enqueue trip-info)
    (println trip-info)
    {:status :success}))

(defn -main []
  (car-mq/worker {} "to-scrape" {:handler run-scraper-worker
                                 :eoq-backoff-ms (constantly 10)
                                 :throttle-ms 10}))
