(ns trainsanywhere.scraper.repl
  (:require [trainsanywhere.scraper.core :as core]
            [trainsanywhere.scraper.web :as web]
            [clj-webdriver.core :as wd]))

;;;; Utilities for interactive exploration of scraping.

(defn run-through [source-name target-name day-str]
  (let [driver (wd/new-driver {:browser :chrome})
        config {:source-name source-name
                :target-name target-name
                :day-str day-str}
        results (web/fetch-route-info driver config)]
    (println results)))
