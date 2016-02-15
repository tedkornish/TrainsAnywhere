(ns trainsanywhere.scraper.web-test
  (:require [clojure.test :refer :all]
            [trainsanywhere.scraper.web :refer :all]))

(deftest test-parse-duration-to-minutes
  (testing "it handles 0 hours"
    (is (= 55 (parse-duration-to-minutes "0hr55min"))))
  (testing "it handles 1-24 hours"
    (is (= 780 (parse-duration-to-minutes "13hr0min"))))
  (testing "it handles more than 24 hours"
    (is (= 1515 (parse-duration-to-minutes "25hr15min"))))
  (testing "it blows up with only minutes"
    (is (thrown? Exception (parse-duration-to-minutes "20min"))))
  (testing "it blows up with only hours"
    (is (thrown? Exception (parse-duration-to-minutes "5hr"))))
  (testing "it blows up with days"
    (is (thrown? Exception (parse-duration-to-minutes "1day3hr10min")))))
