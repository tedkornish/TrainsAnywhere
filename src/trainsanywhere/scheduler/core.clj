(ns trainsanywhere.scheduler.core
  (:require [cronj.core :as c]
            [trainsanywhere.scheduler.routes :refer [routes-task no-routes?]]
            [trainsanywhere.scheduler.queue :refer [queue-task]]))

(def cj (c/cronj :entries [queue-task routes-task]))

(defn sleep-forever []
  (loop []
    (Thread/sleep 100000)
    (recur)))

(defn -main []
  (println "Starting scheduler...")
  (c/start! cj)
  (println "Started.")
  (if (no-routes?) (c/exec! cj "routes-task"))
  (sleep-forever))

