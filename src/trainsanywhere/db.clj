(ns trainsanywhere.db
  (:require [ragtime.repl :as repl]
            [ragtime.jdbc :as jdbc]))

(def postgres-connection-string (System/getenv "TA_POSTGRES"))

(def config
  {:datastore (jdbc/sql-database postgres-connection-string)
   :migrations (jdbc/load-resources "migrations")})

(defn migrate [] (repl/migrate config))
(defn rollback [] (repl/rollback config))
