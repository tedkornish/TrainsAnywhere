(ns trainsanywhere.models
  (:require [trainsanywhere.db :as db]
            [korma.core :as core]))

(declare stations hops trips)

(core/defentity stations)

(core/defentity hops
  (core/belongs-to trips))

;; This has 2 stations that we're leaving out because it's not easy to express
;; joining on the same table twice.
(core/defentity trips
  (core/has-many hops))

(core/defentity routes)
