(defproject trainsanywhere "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.cemerick/url "0.1.1"]
                 [camel-snake-kebab "0.3.2"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "2.0.0"]
                 [org.seleniumhq.selenium/selenium-java "2.47.1"]
                 [org.seleniumhq.selenium/selenium-chrome-driver "2.48.2"] 
                 [clj-webdriver "0.7.2"]]
  :main ^:skip-aot trainsanywhere.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
