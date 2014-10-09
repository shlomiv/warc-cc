;; A modified example based on the first example of clojure-hadoop,
;; that uses warc input files
;;
;; to run:
;;   lein test warc-cc.example
;;
;; This will count the instances of each word in the input warc and write
;; the results to out1/part-00000

(ns warc-cc.example
  (:require [clojure-hadoop.gen     :as gen]
            [clojure-hadoop.imports :as imp]
            [clojure-hadoop.config  :refer [conf]])
  (:import java.util.StringTokenizer
           org.apache.hadoop.util.Tool
           org.apache.hadoop.conf.Configuration
           org.archive.io.ArchiveReader
           org.archive.io.ArchiveRecord)
  (:use clojure.test))

(imp/import-fs)
(imp/import-io)
(imp/import-mapreduce)
(imp/import-mapreduce-lib)
(gen/gen-job-classes)  ;; generates Tool, Mapper, and Reducer classes
(gen/gen-main-method)  ;; generates Tool.main method

(defn file-lines [filename]
  (clojure.string/split-lines (slurp filename)))

;; this turns a tsv file with only one column containing known english words
;; if you dont have it, simply dont use has-known-words, or make it a constant
;; true
(def eng-words? (constantly true))
#_(into #{}
      (rest
       (map #(first (clojure.string/split % #"\t"))
            (file-lines "/home/shlomiv/Downloads/dictionaryWords.tsv"))))

(defn has-known-words [percent ts]
  (< (quot (count ts) percent) (count (filter eng-words? ts))))

(def plain-text? #{"text/plain"})

(defn mapper-map
  [this ^Text key ^ArchiveReader warc-value ^MapContext context]
  (doseq [^ArchiveRecord r warc-value]
    (let [header (.getHeader r)
          mime (.getMimetype header)]
      (if (plain-text? mime)
        (let [tokens (enumeration-seq (StringTokenizer. (str (slurp r))))]
          (when (has-known-words 3 tokens)
            (doseq [word tokens]
              (.write context (Text. word) (LongWritable. 1)))))))))

(defn reducer-reduce
  [this key values ^ReduceContext context]
  (let [sum (reduce + (map (fn [^LongWritable v] (.get v)) values))]
    (.write context key (LongWritable. sum))))

(defn tool-run
  [^Tool this args]
  (doto (Job.)
    (.setJarByClass (.getClass this))
    (conf :name "wordcount1")
    (conf :output-key "org.apache.hadoop.io.Text")
    (conf :output-value "org.apache.hadoop.io.LongWritable")
    (conf :map "warc_cc.example_mapper")
    (conf :reduce "warc_cc.example_reducer")
    (conf :combine "warc_cc.example_reducer")
    (conf :input-format "org.commoncrawl.warc.WARCFileInputFormat") ;; use warc
    (conf :output-format "text")
    (conf :compress-output false)
    (conf :input (first args))
    (conf :output (second args))
    (.waitForCompletion false)
    )
  0)


(deftest test-wordcount-1
  (.delete (FileSystem/get (Configuration.)) (Path. "/media/HD/tmp/out3-new") true)
  (is (tool-run (clojure_hadoop.job.) ["CC-MAIN-20131218054935-00092-ip-10-33-133-15.ec2.internal.warc.wet.gz" "/media/HD/tmp/out3-new"])))

;;(run-tests)

;;
;; bag-of-words:   111.82s user 0.99s system 116% cpu 1:36.54 total 15M
;;
;; cld :           327.39s user 1.43s system 113% cpu 4:49.34 total 21M
;;
;; none:           213.22s user 1.30s system 121% cpu 2:56.63 total 24M
