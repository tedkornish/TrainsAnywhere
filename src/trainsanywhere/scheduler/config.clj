(ns trainsanywhere.scheduler.config)

(def num-stations
  (let [n (Integer/parseInt (System/getenv "TA_NUM_STATIONS"))]
    (if (> 2 n)
      (throw (Exception. "can't use less than 2 stations in scheduler"))
      n)))
