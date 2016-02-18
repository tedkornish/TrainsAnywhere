(defproject trainsanywhere "0.1.0-SNAPSHOT"
  :description "A travel discovery website."
  :url "trainsanywhere.com"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.cemerick/url "0.1.1"]
                 [camel-snake-kebab "0.3.2"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "2.0.0"]
                 [org.seleniumhq.selenium/selenium-java "2.47.1"]
                 [org.seleniumhq.selenium/selenium-chrome-driver "2.48.2"] 
                 [clj-webdriver "0.7.2"]
                 [com.taoensso/carmine "2.12.2"]
                 [org.clojure/data.json "0.2.6"]
                 [ragtime "0.5.2"]
                 [org.clojure/java.jdbc "0.3.7"] ;; korma depends 0.3.7
                 [org.postgresql/postgresql "9.4.1207"]
                 [korma "0.4.2"]
                 [clj-time "0.11.0"]
                 [prismatic/schema "1.0.5"]
                 [com.codeborne/phantomjsdriver "1.2.1"
                    :exclusion [org.seleniumhq.selenium/selenium-java
                                org.seleniumhq.selenium/selenium-server
                                org.seleniumhq.selenium/selenium-remote-driver]]
                 [clojurewerkz/quartzite "2.0.0"]]
  :aliases {"migrate" ["run" "-m" "trainsanywhere.db/migrate"]
            "rollback" ["run" "-m" "trainsanywhere.db/rollback"]
            "seed-stations" ["run" "-m" "trainsanywhere.rail-europe-api/fetch-and-insert-all-stations"]}
  :main ^:skip-aot trainsanywhere.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :scraper {:main trainsanywhere.scraper.core}
             :scheduler {:main trainsanywhere.scheduler.core}
             :persistence {:main trainsanywhere.persistence.core}})
