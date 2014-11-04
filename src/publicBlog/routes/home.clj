(ns publicBlog.routes.home
  (:require [compojure.core :refer :all]
            [publicBlog.layout :as layout]
            [publicBlog.db.core :as db]))

;; maximum number of posts per page
(def per-page 3)

(defn pages [per-page]
  "Calculates the number of pages needed given the maximum number of posts per page"
  (->>
   (if (= 0 (mod (db/count-posts) per-page))
     (quot (db/count-posts) per-page)
     (inc (quot (db/count-posts) per-page)))
   (#(range 1 (inc %)))
   (map #(hash-map :pagenumber %))))

(defn paginator [page per-page]
  "Displays the correct posts given the page number and the maximum number of posts per page"
  (db/display-paginated per-page (* per-page (dec page))))

;; render front-end page templates

(defn home-page [& [error title content post_id tag_name pagenumber]]
  (layout/render
    "home.html" {:error error
                 :title title
                 :content content
                 :post_id post_id
                 :messages (db/display-paginated per-page 0)
                 :pagenumber pagenumber
                 :pages (pages per-page)}))

(defn about-page []
  (layout/render "about.html"))

(defn archive-page [& [error date tag_id]]
  (layout/render
   "archive.html" {:error error
                   :date date
                   :pastposts (db/display-posts)
                   :tag_id tag_id
                   :existingtags (db/existing-tags)}))

(defn post-template [post_id & [error title content date]]
  (layout/render "posts.html"
                 {:error error
                  :title title
                  :content content
                  :date date
                  :getpost (db/get-post post_id)
                  :showtags (db/show-tags post_id)}))

(defn tag-template [tag_id & [error title date tag_name]]
  (layout/render "tags.html"
                 {:error error
                  :title title
                  :date date
                  :showposts (db/show-posts tag_id)
                  :tag_name tag_name
                  :tagname (db/tag-name tag_id)}))

(defn pages-template [pagenumber & [error]]
  (layout/render "pages.html"
                 {:error error
                  :pages (pages per-page)
                  :paginate (paginator (Integer/parseInt pagenumber) per-page)}))

;; Routes

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/archive" [] (archive-page))
  (GET "/post/:post_id" [post_id] (post-template post_id))
  (GET "/pages/:pagenumber" [pagenumber] (pages-template pagenumber)))

(defroutes archive-routes
  (GET "/post/:post_id" [post_id] (post-template post_id))
  (GET "/tag/:tag_id" [tag_id] (tag-template tag_id)))
