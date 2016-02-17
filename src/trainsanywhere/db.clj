(ns trainsanywhere.db
  (:require [ragtime.repl :as repl]
            [ragtime.jdbc :as jdbc]
            korma.db))

(def postgresql-config
  {:user (System/getenv "TA_PSQL_USER")
   :password (System/getenv "TA_PSQL_PASSWORD")
   :host (or (System/getenv "TA_PSQL_HOST") "localhost")
   :port (or (System/getenv "TA_PSQL_PORT") 5432)
   :db (System/getenv "TA_PSQL_DB")})

;; Define the default db for Korma queries.
(korma.db/defdb postgres (korma.db/postgres postgresql-config))

(defn- config-to-string [cfg]
  (let [user-string (str (:user cfg) ":" (:password cfg) "@")
        protocol "jdbc:postgresql://"]
    (str protocol user-string (:host cfg) ":" (:port cfg) "/" (:db cfg))))

(def ragtime-config
  {:datastore (jdbc/sql-database (config-to-string postgresql-config))
   :migrations (jdbc/load-resources "migrations")})

(defn migrate [] (repl/migrate ragtime-config))
(defn rollback [] (repl/rollback ragtime-config))
