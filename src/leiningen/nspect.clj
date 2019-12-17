;; TODO: keep registry of standard names for common nss, like in clj-refactor
;; TODO: consider using bultitude: https://github.com/Raynes/bultitude

;; The lein setup boilerplate was largely borrowed from bikeshed.
;; Also, docstring-checker.

(ns leiningen.nspect
  "Inspect namespaces for consistency and conformance with conventions.

  References:
    - How to NS (style guide), Stuart Sierra
      https://stuartsierra.com/2016/clojure-how-to-ns.html"
  (:require
   [bultitude.core :as b]
   [clojure.tools.cli :as cli]
   [leiningen.core.eval :as lein]
   [leiningen.core.project :as project]))

(defn help
  "Help text displayed from the command line"
  []
  "Inspects your project.")

(defn nspect
  "Main function called from Leiningen"
  [project & args]
  (let [[opts args banner]
        (cli/cli
         args
         ["-H" "--help-me" "Show help"
          :flag true :default false]
         ["-v" "--verbose" "Display missing doc strings"
          :flag true :default false]
         ["-m" "--max-ns-count" "Max line length"
          :parse-fn #(Integer/parseInt %)])
        lein-opts (:nspect project)
        project (if-let [exclusions (seq (:exclude-profiles opts))]
                  (-> project
                      (project/unmerge-profiles exclusions)
                      (update-in [:profiles] #(apply dissoc % exclusions)))
                  project)]
    (if (:help-me opts)
      (println banner)
      (lein/eval-in-project
       (-> project
           (update-in [:dependencies]
                      conj
                      ['nspect "0.1.0-SNAPSHOT"]))
       `(if (nspect.core/nspect
             '~project
             {:max-line-length (or (:max-ns-count ~opts)
                                   (:max-ns-count ~lein-opts))
              :verbose         (:verbose ~opts)
              :check?          #(get (merge ~lein-opts ~opts) % true)})
          (System/exit -1)
          (System/exit 0))
       '(require 'nspect.core)))))
