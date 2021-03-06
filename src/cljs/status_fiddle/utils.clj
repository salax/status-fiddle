(ns status-fiddle.utils
  (:require [clojure.string :as string]
            [hickory.core :as hickory]))

;;TODO this is the copy of status-im.utils.slurp.slurp-svg, but a little bit modifyed,
;;TODO because we need svg as html tags structure

(def svg-tags #{:g :rect :path :use :defs})

(defmacro slurp-web-svg [file]
  (let [svg (-> (clojure.core/slurp file)
                (string/replace #"[\n]\s*" ""))
        svg-hiccup (hickory/as-hiccup (first (hickory/parse-fragment svg)))
        color (gensym "args")]
    `(fn [~color]
       ~(into []
              (clojure.walk/postwalk-replace
                {:viewbox :viewBox} ;; See https://github.com/jhy/jsoup/issues/272
                (clojure.walk/prewalk
                  (fn [node]
                    (if (svg-tags node)
                      node
                      (if (vector? node)
                        (let [[k v] node]
                          (if (and (= :fill k) v)
                            [k color]
                            node))
                        node)))
                  svg-hiccup))))))