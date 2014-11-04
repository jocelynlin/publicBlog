(ns publicBlog.db.schema
  (:use korma.db)
  (:require [clojure.java.jdbc :as sql]
            [clojure.java.io :refer :all]))

(def db-spec (mysql (read-string (slurp "~/dbspec.txt"))))

;; blog post table
(defn create-post-table []
  (sql/db-do-commands
    db-spec
    (sql/create-table-ddl
     :post
     [:post_id "SERIAL PRIMARY KEY"]
     [:title "varchar(255)"]
     [:description "varchar(255)"]
     [:content "TEXT"]
     [:date :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
     [:public :boolean "NOT NULL DEFAULT 0"])))

;; tag table
(defn create-tag-table []
  (sql/db-do-commands
   db-spec
    (sql/create-table-ddl
     :tag
     [:tag_id "SERIAL PRIMARY KEY"]
     [:tag_name "varchar(50)"])))

;; post tags junction table
(defn create-post-tags-table []
  (sql/db-do-commands
   db-spec
    (sql/create-table-ddl
     :post_tags
     [:post_id "bigint(20) NOT NULL REFERENCES post (post_id)"]
     [:tag_id "bigint(20) NOT NULL REFERENCES tag (tag_id)"]
     ["PRIMARY KEY" "(post_id, tag_id)"])))

(defn create-post-tags-index []
  (sql/db-do-prepared db-spec
                      "CREATE INDEX post_index ON post_tags (post_id)")
  (sql/db-do-prepared db-spec
                      "CREATE INDEX tag_index ON post_tags (tag_id)"))

;; user table
(defn create-user-table []
  (sql/db-do-commands
   db-spec
     (sql/create-table-ddl
     :users
     [:id "varchar(32) PRIMARY KEY"]
     [:pass "varchar(100)"])))
