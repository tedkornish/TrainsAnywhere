(defproject trainsanywhere "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.cemerick/url "0.1.1"]
                 [camel-snake-kebab "0.3.2"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "2.0.0"]]
  :main ^:skip-aot trainsanywhere.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
