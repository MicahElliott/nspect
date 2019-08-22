(ns leiningen.nspect
  (:require [clojure.java.io :as io]
            [clojure.tools.namespace.file :as nsfile]
            [clojure.tools.namespace.dir :as nsdir]
            [clojure.tools.namespace.find :as nsfind]
            [clojure.tools.namespace.parse :as nsparse]
            [clojure.tools.reader :as r]
            [clojure.string :as str]
            [clojure.string :as s]
            [clojure.set :as set]))

(defn nspect
  "I don't do a lot."
  [project & args]
  (println "Hi!"))


(def nss (take 3 (nsfind/find-namespaces-in-dir
                  (io/file "/Users/micah.elliott/proj/finops-kstreams/src/finops"))))

(def projdir "src")
(def projdir "/Users/micah.elliott/proj/finops-kstreams/src/finops")
;; Get a bunch of file objects
(def srcs (io/file projdir))

(def allnss
  "Get all the NSs"
  (nsfind/find-ns-decls-in-dir srcs))

;; (split-ns (first allnss))
;; (split-ns (second allnss))
(defn split-ns [nsdecl]
  ;; Strip out docstring
  (let [nsdecl (remove string? nsdecl)]
    (when (< 2 (count nsdecl))
      (let [nsname (second nsdecl)
            decls  (rest (if (= String (type (nth nsdecl 2)))
                           (nth nsdecl 3)
                           (nth nsdecl 2)))]
        (->> decls
             (filter #(and (vector? %)
                           (= :as (second %))))
             (map #(conj [nsname] (first %) (nth % 2))))))))

;; (mapcat split-ns allnss)

;; How to NS (style guide), Stuart Sierra
;; https://stuartsierra.com/2016/clojure-how-to-ns.html

;; NSs to look out for:
;; (ns foons) ; empty
;; (ns foons "foo") ; empty
;; (ns foons [[clojure.string :as str] java-time]) ; no alias
;; (ns foons [[clojure.string :as str :refer [...]) ; also refer
;; (ns foons [[clojure.string :refer :all) ; discouraged refer
;; (ns foons [leiningen.core [eval :as lein] [project :as project]])

(defn check-in
  "Each req is like: [[foons clojure.string str] [barns clojure.string s]]"
  [[nsname reqs]]
  (let [aliases (map #(nth % 2) reqs)]
    (when (not= 1 (count (set aliases)))
      (println "WARN: Inconsistent require aliases for:" nsname)
      (doseq [r reqs]
        (println "-" (last r) "\t" (first r))))))

;; TODO: keep registry of standard names for common nss, like in clj-refactor


;; (check-all allnss)
(defn check-all [allnss]
  (println "Checking" (count allnss) "namespaces for inconsistencies...")
  (map check-in (group-by second (mapcat split-ns allnss))))








;; (def built (group-by first t))
;; {clojure.string [[clojure.string str foons] [clojure.string s barns]]
;;  java-time      [[java-time jt foons] [java-time jt barns]]
;;  logging        [[logging l barns]]}


;; (requires nssyms)
(defn requires
  "Pull just the list of requires, sans `:as`s from a single ns as pairs.
  Ex: [[clojure.string str] [java-time time]]"
  [nssyms]
  (let [nsname (second nssyms)
        ;; Remove the optional docstring
        reqs (if (type (nth nssyms 2))
               (nth nssyms 3)
               (nth nssyms 2))]
    [nsname (map #(mapv % [0 2]) (rest reqs))]))

(defn inspect1
  ""
  []
  (let [nsname (second nssyms)
        pairs  (map #(mapv % [0 2]) (requires nssyms))]
    (doseq [[k nss] (group-by first pairs)]
      (when (< 1 (count nss))
        (println "INCONSISTENCY: namespace" nsname "requiring" k "has" (count nss)
                 "aliases" (map second nss))))))


;; (nsparse/read-ns-decl nssyms)
;; (nsparse/name-from-ns-decl nssyms)
;; (nsparse/deps-from-ns-decl nssyms)
;; (nsparse/ns-decl? nssyms)


;; (let [example (r/read-string
;;                "(ns foo.bar (:require [clojure.string :as str]
;;                                       [clojure.data.csv :as csv]
;;                                       [clojure.data.csv :as csv2]
;;                                       [clojure.data.csv :as c]))")
;;       ;; Grab just the ns and as-alias from all requires.
;;       reqs    (map #(mapv % [0 2]) (rest (nth example 2)))]
;;   (doseq [[k nss] (group-by first reqs)]
;;     (when (< 1 (count nss))
;;       (println "INCONSISTENCY:" k "has" (count nss)
;;                "required namespaces:" (map second nss)))))
