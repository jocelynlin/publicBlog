(defproject
  publicBlog
  "0.1.0-SNAPSHOT"
  :description
  "FIXME: write description"
  :ring
  {:handler publicBlog.handler/app,
   :init publicBlog.handler/init,
   :destroy publicBlog.handler/destroy}
  :ragtime
  {:migrations ragtime.sql.files/migrations,
   :database
   "jdbc:mysql://localhost:3306/publicBlog?user=db_user_name_here&password=db_user_password_here"}
  :plugins
  [[lein-ring "0.8.10"]
   [lein-environ "0.5.0"]
   [lein-ancient "0.5.5"]
   [ragtime/ragtime.lein "0.3.6"]]
  :url
  "http://example.com/FIXME"
  :profiles
  {:uberjar {:aot :all},
   :production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
   {:dependencies
    [[ring-mock "0.1.5"]
     [ring/ring-devel "1.3.1"]
     [pjstadig/humane-test-output "0.6.0"]],
    :injections
    [(require 'pjstadig.humane-test-output)
     (pjstadig.humane-test-output/activate!)],
    :env {:dev true}}}
  :jvm-opts
  ["-server"]
  :dependencies
  [[lib-noir "0.8.9"]
   [log4j
    "1.2.17"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [com.mchange/c3p0 "0.9.2.1"]
   [markdown-clj "0.9.47"]
   [mysql/mysql-connector-java "5.1.6"]
   [prone "0.6.0"]
   [noir-exception "0.2.2"]
   [com.taoensso/timbre "3.3.1"]
   [com.taoensso/tower "3.0.1"]
   [hiccup "1.0.5"]
   [korma "0.4.0"]
   [selmer "0.7.1"]
   [org.clojure/clojure "1.6.0"]
   [org.clojure/java.jdbc "0.3.5"]
   [environ "1.0.0"]
   [ring-server "0.3.1"]
   [ragtime "0.3.6"]
   [im.chit/cronj "1.4.2"]
   [compojure "1.1.9"]]
  :repl-options
  {:init-ns publicBlog.repl}
  :min-lein-version "2.0.0")
