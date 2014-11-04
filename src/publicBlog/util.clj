(ns publicBlog.util
  (:require [noir.io :as io]
            [markdown.core :as md])
  (:import java.util.TimeZone))

(defn md->html
  "reads a markdown file from public/md and returns an HTML string"
  [filename]
  (md/md-to-html-string (io/slurp-resource filename)))

