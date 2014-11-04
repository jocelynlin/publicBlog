(ns publicBlog.routes.auth
  (:require [hiccup.form :refer :all]
            [compojure.core :refer :all]
            [publicBlog.routes.home :refer :all]
            [publicBlog.layout :as layout]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as vali]
            [noir.util.crypt :as crypt]
            [publicBlog.db.core :as db]))

;; Registration page, remove this code and all routes pointing to this code
;; after creating needed user accounts
(defn valid? [id pass pass1]
  (vali/rule (vali/has-value? id)
             [:id "User id is required"])
  (vali/rule (vali/min-length? pass 5)
             [:pass "password must be at least 5 characters"])
  (vali/rule (= pass pass1)
             [:pass "entered passwords do not match"])
  (not (vali/errors? :id :pass :pass1)))

(defn format-error [id ex]
  (cond
   (and (instance? java.sql.SQLException ex)
        (= 1062 (.getErrorCode ex)))
   (str "The user with id " id " already exists!")
   :else
   "An error has occured while processing the request."))

(defn registration-page [& [id]]
  (layout/render "registration.html"
                 {:id id
                  :id-error (first (vali/get-errors :id))
                  :pass-error (first (vali/get-errors :pass))}))

(defn handle-registration [id pass pass1]
  (if (valid? id pass pass1)
    (try
      (db/create-user {:id id :pass (crypt/encrypt pass)})
      (session/put! :user id)
      (resp/redirect "/login")
      (catch Exception ex
        (vali/rule false [:id (format-error id ex)])
        (registration-page)))
    (registration-page id)))

;; Login
(defn login []
  (layout/render "login.html"))

(defn handle-login [id pass]
  (let [user (db/get-user id)]
    (if (and user (crypt/compare pass (:pass user)))
      (session/put! :user id)))
  (resp/redirect "/post-page"))

;; Logout
(defn handle-logout []
  (session/clear!)
  (resp/redirect "/"))


(defroutes auth-routes
  (GET "/login" []
       (login))
  (GET "/register" []
       (registration-page))
  (POST "/register" [id pass pass1]
        (handle-registration id pass pass1))
  (POST "/login" [id pass]
        (handle-login id pass))
  (GET "/logout" []
        (handle-logout)))

