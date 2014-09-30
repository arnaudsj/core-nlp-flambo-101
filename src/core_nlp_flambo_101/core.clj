(ns core-nlp-flambo-101.core
  (:require  [flambo.api :as f]
             [flambo.streaming :as fs]
             [flambo.conf :as fconf]
             [clj-time.format :as tf]
             [clj-time.core :as tc]
             [clojure.tools.trace :refer [trace]]
             [clojure.java.shell :refer [sh]]
             [clojure.pprint :refer [pprint]]
             [org.satta.glob :refer [glob]])
  (:import   [org.apache.spark.streaming.twitter TwitterUtils]
             [org.apache.log4j Level Logger])
  (:gen-class))

(def master "local[*]")

(def conf {})

(def env {
          "spark.executor.memory" "1G",
          "spark.files.overwrite" "true"
          })

;; We don't need to see everything ;
(.setLevel (Logger/getRootLogger) Level/WARN)

(def ^:dynamic *app-consumer-key* "2BgYuLbLSmN43UckjIAtQT232")
(def ^:dynamic *app-consumer-secret* "PN2bVs5LjgIHDDjiMz9NWslUTNwFqiZEr3SS88zPLLRHfrQtGg")
(def ^:dynamic *user-screen-name* "arnaudsj")
(def ^:dynamic *user-access-token* "13253662-UfOTvrBCMb9GokXUhhn0p5P7ctxLYzBU9EEu3zbbk")
(def ^:dynamic *user-access-token-secret* "lT4o5CY4tseOPwDQvbY6LSiZ7PRuIja7ulJW32rme9RCk")

(System/setProperty "twitter4j.oauth.consumerKey", *app-consumer-key*)
(System/setProperty "twitter4j.oauth.consumerSecret", *app-consumer-secret*)
(System/setProperty "twitter4j.oauth.accessToken", *user-access-token*)
(System/setProperty "twitter4j.oauth.accessTokenSecret", *user-access-token-secret*)

(defn new-spark-context []
  (let [c (-> (fconf/spark-conf)
              (fconf/master master)
              (fconf/app-name "core-nlp-flambo-101")
              (fconf/set "spark.akka.timeout" "300")
              (fconf/set conf)
              (fconf/set-executor-env env))]
    (fs/streaming-context c 2000) ))

(defonce sc (new-spark-context))

(fs/checkpoint sc "/tmp/")

(defonce tweet-stream (TwitterUtils/createStream sc))

;; ==================
;; Helper functions
;; ==================

(defn extract-hashtag [s]
  (re-find (re-pattern "#\\w+\\b") s))

;; ==================
;; Stream processing
;; ==================

;; val hashTags = stream.flatMap(status => status.getText.split(" ").filter(_.startsWith("#")))
 (defonce hashTags (fs/flat-map tweet-stream (fn[t] (filter #(< 0 (count %)) [(extract-hashtag (.getText t))]))))

; val topCounts60 = hashTags.map((_, 1)).reduceByKeyAndWindow(_ + _, Seconds(60))
;                      .map{case (topic, count) => (count, topic)}
;                      .transform(_.sortByKey(false))

; (defonce topCounts30 (fs/reduce-by-key-and-window hashTags (f/fn [[_ + _]]) 30000 30000))

;; =============
;; Stream output
;; =============
(defn -main []
  (fs/print hashTags)
  (.start sc)
  (.awaitTermination sc))


; (fs/foreach-rdd tweets
  ; (fn[rdd t] fs/map rdd (fn[t] {:status (.getText t)}))
  ; (-> tweets
      ; (f/map (f/fn[[r d]] 1))
;;      (fn[rdd t] (fs/map rdd (fn[t] {:status (.getText t)})))
  ; ))

;; See the following for reference on the options available for rdd returned
;; https://github.com/yusuke/twitter4j/blob/master/twitter4j-core/src/main/java/twitter4j/Status.java
; (fs/print (fs/map tweets (fn[t] {:status (.getText t)})))
