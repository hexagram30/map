(ns hxgm30.map.biome.elevation
  (:require
    [clojure.java.io :as io]
    [hxgm30.map.io :as map-io]))

(def elevation-file "001-mercator-offset-elevation-bw-blackwater")

(defn read-elevation
  []
  (map-io/read-png elevation-file))
