(ns nspect.core
  (:require
   [clojure.java.io :as io]
   [clojure.tools.namespace.find :as nsfind]))



(def nss (take 3 (nsfind/find-namespaces-in-dir
                  (io/file "/Users/micah.elliott/proj/finops-kstreams/src/finops"))))

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


;; (check-all allnss)
(defn check-all [allnss]
  (println "Checking" (count allnss) "namespaces for inconsistencies...")
  (map check-in (group-by second (mapcat split-ns allnss))))


(defn visible-project-files []
  (let [projdir "src"
        projdir "/Users/micah.elliott/proj/finops-kstreams/src/finops"
        ;; Get a bunch of file objects
        srcs    (io/file projdir)
        ;; Get all the NSs
        allnss (nsfind/find-ns-decls-in-dir srcs)]
    allnss))

(defn nspect
  [project {:keys [verbose max-ns-count]}]
  (check-all (visible-project-files)))
