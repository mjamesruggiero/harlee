(ns harlee.core
  (:require [clojure.java.jdbc :as sql]
            [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn load-config
  "Loads EDN config from a file"
  [filename]
  (edn/read-string (slurp filename)))

(defn db-config
  "Load db config; defaults to Dev config but will optionally
  take key/val pair :config-file <file-name>
  (e.g. 'test-config.edn')"
  [& {:keys  [config-file]
      :or  {config-file  "dev-config.edn"}}]
  (let [file-path (or (System/getenv "HARLEE_CONFIG") config-file)
        conf (load-config (io/resource file-path))]
    {:classname "org.postgresql.Driver"
     :subprotocol "postgresql"
     :subname (:subname conf)
     :user (:user conf)
     :password (:password conf)}))

(defn get-testtable [db]
  (sql/with-connection db
    (sql/with-query-results
      res ["select * from testtable"] (first res))))

(let [db (db-config)]
  (get-testtable db))
