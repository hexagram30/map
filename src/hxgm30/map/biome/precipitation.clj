(ns hxgm30.map.biome.precipitation
  (:require
    [clojure.java.io :as io]
    [hxgm30.map.io :as map-io]))

(def mm-in-mils 0.0254)

(def precipitation-file "001-mercator-offset-precipitation")

(defn read-precipitation
  []
  (map-io/read-png precipitation-file))

(defn mils->mm
  [mils]
  (* mils mm-in-mils))
