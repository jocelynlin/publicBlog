(ns publicBlog.routes.postpage
  (:require [compojure.core :refer :all]
            [publicBlog.routes.auth :refer :all]
            [publicBlog.layout :as layout]
            [noir.response :as resp]
            [noir.util.route :refer [restricted]]
            [noir.validation :as vali]
            [publicBlog.db.core :refer [save-post
                                  tag-check
                                  create-tag
                                  existing-tags
                                  associate-tag
                                  show-tags
                                  edit-post
                                  publish-post
                                  display-last-post]]))

(declare post-page tag-page edit-page)

(defn validpost? [title content]
  (vali/rule (vali/has-value? title)
             [:title (str "Title cannot be empty")])
  (vali/rule (vali/has-value? content)
             [:content (str "Content cannot be empty")])
  (not (vali/errors? :title :content)))

(defn validtag? [post_id tag_name]
  (vali/rule (vali/has-value? post_id)
             [:post_id (str "Post ID is empty")])
  (vali/rule (vali/has-value? tag_name)
             [:tag_name (str "Tag name is empty")])
  (not (vali/errors? :post_id :tag_name)))

;; post-page functions

(defn post-page []
  (layout/render "post-page.html"))

(defn handle-post [title description content]
  (if (validpost? title  content)
    (do (save-post title description content)
      (resp/redirect "/tag-page"))
    (post-page title)))

(defroutes post-page-routes
  (GET "/login" [] (login))
  (GET "/post-page" [] (restricted (post-page)))
  (GET "/tag-page" [info] (restricted (tag-page info)))
  (GET "/edit-page" [info] (restricted (edit-page info)))
  (POST "/post-page" [title description content]
        (restricted (handle-post title description content)))
  (GET "/logout" []
        (handle-logout)))

;; tag-page functions

(defn tag-page [& [error post_id title description content date public tag_name]]
  (layout/render "tag-page.html"
                 {:error error
                  :post_id post_id
                  :title title
                  :description description
                  :content content
                  :date date
                  :public public
                  :lastpost (display-last-post)
                  :tag_name tag_name
                  :tags (existing-tags)
                  :showtags (show-tags (map :post_id (display-last-post)))}))

;; handle-tag - checks if validtag, if the tag_name does not exist, creates the tag_name
;; and associates the tag_name to the post_id.  if the tag_name exists, associates the
;; tag_name to the post_id.
(defn handle-tag [post_id tag_name]
  (if (validtag? post_id tag_name)
    (try
      (if (empty? (tag-check tag_name))
        (do (create-tag tag_name)
          (associate-tag post_id tag_name)
          (tag-page))
        (do
          (associate-tag post_id tag_name)
          (tag-page)))
      (catch Exception ex (tag-page (str "Caught Exception: "(.getMessage ex)))))
    (tag-page post_id tag_name)))

(defroutes tag-page-routes
  (GET "/login" [] (login))
  (GET "/post-page" [info] (restricted (post-page info)))
  (GET "/tag-page" [info] (restricted (tag-page info)))
  (GET "/edit-page" [info] (restricted (edit-page info)))
  (POST "/tag-page" [post_id tag_name]
        (restricted (handle-tag post_id tag_name)))
  (GET "/logout" []
        (handle-logout)))

;; edit page functions

(defn edit-page [& [error post_id title description content date public]]
  (layout/render "edit-page.html"
                 {:error error
                  :post_id post_id
                  :title title
                  :description description
                  :content content
                  :date date
                  :public public
                  :lastpost (display-last-post)}))

;; handle-edit - if title, description and content are empty, toggles the post public
;; or private depending if public is checked.  if any of title, description, or content
;; have values, update all of those fields, and toggles the post public or private
;; depending if public is checked.  TODO: modify the function to update individual
;; fields instead of all the fields at once.
(defn handle-edit [post_id title description content public]
  (if (and (empty? title) (empty? description) (empty? content))
    (if (empty? public)
      (publish-post post_id false)
      (publish-post post_id true))
    (do (edit-post post_id title description content)
      (if (empty? public)
        (publish-post post_id false)
        (publish-post post_id true))))
  (edit-page post_id))

(defroutes edit-page-routes
  (GET "/login" [] (login))
  (GET "/post-page" [info] (restricted (post-page info)))
  (GET "/tag-page" [info] (restricted (tag-page info)))
  (GET "/edit-page" [info] (restricted (edit-page info)))
  (POST "/edit-page" [post_id title description content public]
        (restricted (handle-edit post_id title description content public)))
  (GET "/logout" []
        (handle-logout)))
