(defproject core-nlp-flambo-101 "0.1.0-SNAPSHOT"
  :description "Example of using Flambo with core.nlp"
  :url "http://arnaudsj.github.io/core.nlp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [yieldbot/flambo "0.3.3"]
                 [org.apache.spark/spark-core_2.10 "1.0.2"]
                 ; [org.apache.spark/spark-streaming_2.10 "1.0.1"]
                 ; [org.apache.spark/spark-streaming-kafka_2.10 "1.0.1"]
                 ; [org.apache.spark/spark-sql_2.10 "1.0.1"]
                 [clj-time "0.8.0"]
                 [org.clojure/tools.trace "0.7.8"]
                 [clj-glob "1.0.0"]
                 ]
  :main core-nlp-flambo-101.core
  :profiles {:dev
                {:aot [core-nlp-flambo-101.core]}
             :uberjar
                {:aot :all}})
