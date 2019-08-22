(ns nspect-test
  (:require  [clojure.test :as t]
             [leiningen.nspect :as sut]))


(def t [
        ['foons 'clojure.string 'str] ; bad
        ['foons 'java-time      'jt ] ; ok
        ['barns 'java-time      'jt ] ; ok
        ['barns 'clojure.string 's ] ; bad
        ['barns 'logging        'l ]
        ])


(map sut/check-in (group-by second t))


#_[((ns bar.ach-return-workflow "Topology for the ACH return workflow."
        (:require [clojure.tools.logging :as log]
                  [com.foo.ach-return :as ach-return]
                  [com.foo.kafka-message :as kafka-message]
                  [bar.streams.patch :as p]
                  [bar.topics :as topics]))
    (ns bar.aero "Some aero enhancements."
        (:require [aero.core :as aero]
                  [clojure.java.io :as io]
                  [clojure.string :as str]
                  [environ.core :refer [env]]
                  bar.time
                  [java-time :as time])))]

(let [example (r/read-string
               "(ns foo.bar (:require [clojure.string :as str]
                                      [clojure.data.csv :as csv]
                                      [clojure.data.csv :as csv2]
                                      [clojure.data.csv :as c]))")])
