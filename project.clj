(defproject warc-cc "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0" ]
                 [clojure-hadoop/clojure-hadoop "1.4.4"
                  :exclusions [clojure-complete commons-httpclient]]
                 [org.netpreserve.commons/webarchive-commons "1.1.4"
                  :exclusions [org.apache.hadoop/hadoop-core commons-lang commons-logging log4j commons-configuration]]
                 ]
  :java-source-paths ["java"]
  :javac-options  ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot :all)
