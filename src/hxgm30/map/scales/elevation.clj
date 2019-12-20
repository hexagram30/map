(ns hxgm30.map.scales.elevation
  (:require
   [clojure.string :as string]
   [hxgm30.map.io :as map-io]
   [hxgm30.map.scales.util :as scales-util]
   [hxgm30.map.units :as units]
   [hxgm30.map.util :as util]
   [taoensso.timbre :as log])
  (:refer-clojure :exclude [get-ranges min max]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Elevation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def min 0) ; in strides (~2 leagues or ~7 miles)
(def max units/highest-mountain) ; in strides (~2 leagues or ~7 miles)
(def file "ilunao/elevation-scale-hex")
(def elevation (scales-util/read-scale-txt file))
(def colors
  (memoize
   (fn [] (scales-util/scale-colors-txt elevation))))

(def strides-per-grade (float (/ (- max min)
                                 (dec (count (colors))))))

(defn ranges
  ""
  []
  (scales-util/get-ranges min max strides-per-grade))

(def lookup
  (memoize
   (fn []
     (->> (scales-util/lookup-grouping (colors) (ranges))
          (map vec)
          (into {})))))

(def reverse-lookup
  (memoize
   (fn []
     (->> (scales-util/lookup-grouping (colors) (ranges))
          (map (comp vec reverse))
          (into {})))))

(defn check
  [strides]
  (cond (< strides min)
        min

        (> strides max)
        max

        :else
        strides))

(defn find-range
  [strides]
  (scales-util/find-range (check strides) (ranges)))

(defn color
  [strides]
  (get (lookup) (find-range strides)))

(defn elevation-amount
  "Given an RGB color hashmap, return the corresponding elevation."
  [color-map]
  (->> color-map
       (get (reverse-lookup))
       ((fn [x] (log/trace "Elevation amount:" x) x))
       (util/mean)))

(defn print-colors
  ([]
   (print-colors min max 1000))
  ([start stop step]
   (let [output (mapv #(println (str (format "%,-6d strides : " %)
                                     (util/color-map->ansi
                                      (color %))))
                      (range start (+ stop step) step))]
     (println (string/join output)))))

(defn coord->elevation
  [im x y]
  (log/debugf "Getting elevation for [%s, %s] ..." x y)
  (-> (map-io/rgb im x y)
      ((fn [x] (log/trace "RGB pixel:" x) x))
      util/rgb-pixel->color-map
      ((fn [x] (log/trace "Color map:" x) x))
      elevation-amount))

(defn elevation->pixel
  [strides]
  (-> strides
      color
      util/color-map->rgb-pixel))
