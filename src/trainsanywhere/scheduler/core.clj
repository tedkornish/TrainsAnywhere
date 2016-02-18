(ns trainsanywhere.scheduler.core
  (:require [cronj.core :as c]
            [trainsanywhere.scheduler.routes :refer [routes-task]]
            [trainsanywhere.scheduler.queue :refer [queue-task]]))

(def cj (c/cronj :entries [queue-task routes-task]))

(defn -main []
  (c/start! cj))
