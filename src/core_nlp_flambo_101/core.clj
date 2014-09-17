(ns core-nlp-flambo-101.core
  (:require  [flambo.api :as f]
             [flambo.conf :as fconf]
             [clj-time.format :as tf]
             [clj-time.core :as tc]
             [clojure.tools.trace :refer [trace]]
             [clojure.java.shell :refer [sh]]
             [clojure.pprint :refer [pprint]]
             [org.satta.glob :refer [glob]])
  (:gen-class))

(def master "local[*]")
(def conf {})
(def env {
          "spark.executor.memory" "4G",
          "spark.files.overwrite" "true"
          })

(defn new-spark-context []
  (let [c (-> (fconf/spark-conf)
              (fconf/master master)
              (fconf/app-name "core-nlp-flambo-101")
              (fconf/set "spark.akka.timeout" "300")
              (fconf/set conf)
              (fconf/set-executor-env env))]
    (f/spark-context c) ))

(defonce sc (delay (new-spark-context)))




;; REPL functions

(defn hello-world []
  (print "Hello, world!"))
