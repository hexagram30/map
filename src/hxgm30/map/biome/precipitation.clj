(ns hxgm30.map.biome.precipitation
  (:require
    [hxgm30.map.io :as map-io]
    [hxgm30.map.scales.core :as scales]))

(def mm-in-mils 0.0254)

(def precipitation-file "ilunao/precipitation")
(def precipitation-tiny-file "ilunao/precipitation-tiny")
(def ps (scales/new-scale :precipitation :exponential 1.2))
;(def ps (scales/new-scale :precipitation :linear))

(defn read-precipitation
  []
  (map-io/read-png precipitation-file))

(defn read-precipitation-tiny
  []
  (map-io/read-png precipitation-tiny-file))

(defn mils->mm
  [mils]
  (* mils mm-in-mils))
