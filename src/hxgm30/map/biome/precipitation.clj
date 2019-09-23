(ns hxgm30.map.biome.precipitation
  (:require
    [clojure.java.io :as io]
    [hxgm30.map.io :as map-io]))

(def precipitation-file "001-mercator-offset-precipitation.png")

(defn read-precipitation
  []
  (map-io/read-png precipitation-file))
