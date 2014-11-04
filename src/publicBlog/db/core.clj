(ns publicBlog.db.core
  (:use korma.core
        clojure.walk
        [korma.db :only (defdb)]
        [taoensso.timbre :only [trace debug info warn error fatal]])
  (:require [compojure.core :refer :all]
            [publicBlog.db.schema :as schema]
            [clojure.java.jdbc :as sql]
            [clojure.string :refer [lower-case]])
  (:gen-class
   :methods [#^{:static true} [javaFunction [java.util.Map] void]]))

(defdb db schema/db-spec)

(defentity users)

(defentity post)

(defentity tag)

(defentity post_tags)

(declare tag-check)

;; create user - accepts a map of :id and :pass
(defn create-user [user]
  (insert users
          (values user)))

;; user login
(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))

;;save blog post to the database

(defn save-post [title description content]
  (let [postkey (insert post (values {:title title
                                      :description description
                                      :content content}))]
          (debug postkey)
          postkey))

;; edit blog post

(defn edit-post [post_id title description content]
  (update post
          (set-fields {:title title
                       :description description
                       :content content})
          (where {:post_id post_id})))

;; display last blog post to the database
(defn display-last-post []
  (select post
          (fields :post_id :title :description :content :date :public)
          (order :post_id :DESC)
          (limit 1)))

;; get post by id

(defn get-post [post_id]
  (select post
          (fields :post_id :title :description :content :date)
          (where {:post_id post_id
                  :public true})))

;; display public posts

(defn display-posts []
  (select post
          (fields :title :description :content :date :post_id)
          (where {:public true})
          (order :date :DESC)))

;; display paginated posts
(defn display-paginated [limitby offsetby]
  (select post
          (fields :title :description :content :date :post_id)
          (where {:public true})
          (order :date :DESC)
          (limit limitby)
          (offset offsetby)))

;; gives a count of all the public posts

(defn count-posts []
  (->>
   (select post
           (aggregate (count :*) :post_id)
           (where {:public true}))
   (map #(get % :post_id))
   (#(nth % 0))))

;; delete blog post

(defn delete-post [post_id]
  (delete post
          (where {:post_id post_id})))

;; publish blog post

(defn publish-post [post_id public]
  (update post
          (set-fields {:public public})
          (where {:post_id post_id})))

;; save tag to database

(defn create-tag [tag_name]
  (let [tagkey (insert tag (values {:tag_name (lower-case tag_name)}))]
    (debug tagkey)
    tagkey))

;; associate tags to a blog post

(defn associate-tag [post_id tag_name]
  (let [{tag_id :tag_id} (into {} (tag-check tag_name))]
    (insert post_tags (values {:post_id post_id
                               :tag_id tag_id}))))

;; display all existing tags
(defn existing-tags []
  (select tag
          (fields :tag_id :tag_name)
          (order :tag_name :ASC)))

;; display tag names given the tag_ids
(defn tag-name [tag_id]
  (select tag
          (fields :tag_id :tag_name)
          (where {:tag_id tag_id})))

;; show tag names and tag ids associated with a post_id
(defn show-tags [post_id]
  (->> (select post_tags
          (fields :tag_id)
          (where {:post_id post_id}))
       (map #(get % :tag_id)) ;; returns list of tag_id values
       (map tag-name) ;; maps the tag-name function over the list of tag_id values
       (flatten)))

;; show all posts associated with a certain tag_id
(defn show-posts [tag_id]
  (->> (select post_tags
               (fields :post_id)
               (where {:tag_id tag_id})
               (order :post_id :DESC))
       (map #(get % :post_id))
       (map get-post)
       (flatten)))

;; check if the tag exists

(defn tag-check [tag_name]
  (select tag
          (where {:tag_name tag_name})))
