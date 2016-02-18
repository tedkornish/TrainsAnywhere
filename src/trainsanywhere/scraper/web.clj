(ns trainsanywhere.scraper.web
  (:require [clj-webdriver.core :as wd]
            [clj-webdriver.taxi :as taxi :only [find-elements]]
            [clojure.string :as str]))

;;;; This file contains utilities for scraping trip and hop data out of the
;;;; Rail Europe website.

(def request-url
  "https://www2.raileurope.com/us/rail/point_to_point/triprequest.htm")

(defn- send-wait-and-enter [elem text]
  (wd/send-keys elem text)
  (Thread/sleep 1000)
  (wd/send-keys elem "\n"))

(defn- find-css
  "Finds an element from a driver (or subelement from an element) given a CSS
  selector."
  [driver selector] 
  (wd/find-element driver {:css selector}))

(defn- find-css-many
  "Finds an element from a driver (or subelement from an element) given a CSS
  selector."
  [driver selector] 
  (taxi/find-elements driver {:css selector}))

(defn navigate-to-prices-for [driver source target day]
  (wd/to driver request-url)
  (send-wait-and-enter (find-css driver "#from0") source)
  (send-wait-and-enter (find-css driver "#to0") target)
  (wd/send-keys (find-css driver "span.departure-date input") day)
  (wd/click (find-css driver "span.time select option[value='0']"))
  (wd/click (find-css driver "#fs-submit"))
  (Thread/sleep 10000) ;; give it time to load; TODO make this cleaner
  driver)

(defn parse-duration-to-minutes
  "Parses a string like 0hr55min or 1hr23min into a number of minutes."
  [dur] 
  (let [hrs-and-min-strings (-> dur (str/replace "min" "") (str/split #"hr"))
        [hrs mins] (map read-string hrs-and-min-strings)]
    (+ (* 60 hrs) mins)))

(defn parse-hops
  "Parse some hop objects out of a train info element."
  [train-info] 
  (let [departure-elem (find-css train-info ".be-train-info .be-depart-col")
        arrival-elem (find-css train-info ".be-train-info .be-arrive-col")]
    {:departure-time (wd/text (find-css departure-elem ".time"))
     :departure-station (wd/text (find-css departure-elem ".station"))
     :arrival-time (wd/text (find-css arrival-elem ".time"))
     :arrival-station (wd/text (find-css arrival-elem ".station"))
     :duration (-> (find-css train-info ".be-train-info .be-dur-col")
                   wd/text
                   (clojure.string/replace "\n" "")
                   parse-duration-to-minutes)}))

(defn- map-function-on-map-vals
  "Applies a given function to every value in a map."
  [m f]
  (apply merge (map (fn [[k v]] {k (f v)}) m)))

(defn- dollars-to-number [dollar-string]
  (if-not (nil? dollar-string)
    (read-string (str/replace dollar-string "$" ""))))

(defn extract-trip-from-result-elem
  "Given a result element, parse train info, prices, and hops and layovers out
  of the element's DOM."
  [elem] 
  (let [train-info (find-css elem ".be-train-info")
        price-elems (find-css-many elem ".tiered-tabs span.price")
        [economy comfort premiere] (map wd/text price-elems)
        hop-elems (filter wd/exists? (find-css-many elem ".tiered-row.multi"))
        hops (if (empty? hop-elems)
               [(parse-hops train-info)]
               ;; even indices are hops, odd are layovers 
               (map parse-hops (take-nth 2 hop-elems))) 
        prices {:economy economy :comfort comfort :premiere premiere}]
    {:hops hops :prices (map-function-on-map-vals prices dollars-to-number)}))


(defn fetch-route-info
  "Day should be formatted like 1/21/2016."
  [driver {source :source-name target :target-name day :day-str :as orig}]
  (navigate-to-prices-for driver source target day)
  (let [result-elems (find-css-many driver "div.tiered-row.shadowbox")
        trips (doall (map extract-trip-from-result-elem result-elems))] 
    {:original orig :trips trips}))
