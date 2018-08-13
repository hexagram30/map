(ns hxgm30.map.config
  (:require
   [hxgm30.common.file :as common]))

(def config-file "hexagram30-config/map.edn")

(defn data
  ([]
    (data config-file))
  ([filename]
    (common/read-edn-resource filename)))
