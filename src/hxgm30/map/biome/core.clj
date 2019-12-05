(ns hxgm30.map.biome.core
  (:require
    [clojure.data.avl :as avl]
    [hxgm30.map.biome.temperature :as temperature]
    [hxgm30.map.util :as util]))

(def biome-temps (conj (map #(+ temperature/K (* (Math/pow 2 %) 1.5))
                            (concat (range 7) [3.4 4.6 5.6]))
                       temperature/K))
(def biome-precips (map #(* (Math/pow 2 %) 62.5) (range 9)))
(def sorted-temps (apply avl/sorted-set biome-temps))
(def sorted-precips (apply avl/sorted-set biome-precips))

(def nearest-temp #(util/nearest sorted-temps %))
(def nearest-precip #(util/nearest sorted-precips %))

;; XXX create matrix of biome data, integers representing the elements in
;;     resources/planets/001-mercator-biomes.edn ... this should probably be
;;     a memoized fn
;; XXX create lookup where the keys are the temp/precip coords and the values
;;     are the corresponding elements of the biome data matrix; should probably
;;     also be a memoized fn
;; XXX create nearest-biome fn that uses the lookup matrix and the nearest-*
;;     fns
