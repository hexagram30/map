(ns hxgm30.map.biome.core
  (:require
    [clojure.data.avl :as avl]
    [hxgm30.map.biome.temperature :as temperature]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.util :as util]))

(def gen-temp-step #(+ temperature/K (* (Math/pow 2 %) 1.5)))
(def gen-precip-step #(* (Math/pow 2 %) 62.5))
(def biome-temps (conj (map gen-temp-step (concat (range 7) [3.4 4.6 5.6]))
                       temperature/K))
(def biome-precips (map gen-precip-step (range 9)))
(def cols (count biome-precips))
(def rows (count biome-temps))
(def sorted-temps (apply avl/sorted-set biome-temps))
(def sorted-precips (apply avl/sorted-set biome-precips))

(def nearest-temp #(util/nearest sorted-temps %))
(def nearest-precip #(util/nearest sorted-precips %))

(def biomes (map-io/read-edn "001-mercator-biomes"))
(def biomes-indexed (map-indexed #(vector %1 %2) biomes))
(def biomes-matrix [
  [ 0  0  0  0  0  0  0  0  0]
  [ 1  2  3  4  4  4  4  4  4]
  [ 5  6  7  8  8  8  8  8  8]
  [ 9 14 27 32 37 37 37 37 37]
  [10 15 19 28 33 38 38 38 38]
  [11 16 20 24 29 34 39 39 39]
  [12 17 21 25 30 35 40 40 40]
  [13 18 22 23 26 31 36 41 41]
  [13 13 18 22 23 26 31 36 41]
  [13 13 13 18 22 23 26 31 36]
  [13 13 13 13 18 22 23 26 31]])

;; XXX create lookup where the keys are the temp/precip coords and the values
;;     are the corresponding elements of the biome data matrix; should probably
;;     also be a memoized fn
;; XXX create nearest-biome fn that uses the lookup matrix and the nearest-*
;;     fns

(defn print-colors-row
  [row]
  (doseq [elem row]
    (print (util/color-map->ansi
              (util/hex->color-map (:color (nth biomes elem)))
              util/ansi-square)))
  (println))

(defn print-colors-matrix
  []
  (doseq [row biomes-matrix]
    (print-colors-row row)))
