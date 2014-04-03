(defproject warc-cc "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [clojure-hadoop/clojure-hadoop "1.4.4"]
                 [org.netpreserve.commons/webarchive-commons "1.1.2"]]
  :java-source-paths ["java"]
  :javac-options  ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot :all
  )
