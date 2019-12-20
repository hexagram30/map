(ns hxgm30.map.scales.precipitation
  (:require
   [clojure.string :as string]
   [hxgm30.map.io :as map-io]
   [hxgm30.map.scales.util :as scales-util]
   [hxgm30.map.util :as util]
   [taoensso.timbre :as log])
  (:refer-clojure :exclude [get-ranges min max]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Precipitation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def default-min 0) ; in mils/year (~4500 mm/yr)
(def default-max 640000) ; in mils/year, ~12,000 mil/week (~16,000 mm/yr)
(def file "ilunao/precipitation-scale-hex")
(def precipitation (scales-util/read-scale-txt file))
(def colors
  (memoize
   (fn [] (scales-util/scale-colors-txt precipitation))))

(def milyr-per-grade (float (/ (- default-max default-min)
                               (dec (count (colors))))))

(defn ranges
  ""
  []
  (scales-util/get-ranges default-min default-max milyr-per-grade))

(def lookup
  (memoize
   (fn []
     (->> (scales-util/lookup-grouping
           (colors) (ranges))
          (map vec)
          (into {})))))

(def reverse-lookup
  (memoize
   (fn []
     (->> (scales-util/lookup-grouping
           (colors) (ranges))
          (map (comp vec reverse))
          (into {})))))

(defn check
  [milyr]
  (cond (< milyr default-min)
        default-min

        (> milyr default-max)
        default-max

        :else
        milyr))

(defn find-range
  [milyr]
  (scales-util/find-range (check milyr) (ranges)))

(defn color
  [milyr]
  (get (lookup) (find-range milyr)))

(defn precipitation-amount
  "Given an RGB color hashmap, return the corresponding annual precipitation."
  [color-map]
  (->> color-map
       (get (reverse-lookup))
       ((fn [x] (log/trace "Precipitation amount:" x) x))
       (util/mean)))

(defn print-colors
  ([]
   (print-colors default-min default-max 43000))
  ([start stop step]
   (let [output (mapv #(println (str (format "%,-7d mils/year : " %)
                                     (util/color-map->ansi
                                      (color %))))
                      (range start (+ stop step) step))]
     (println (string/join output)))))

(defn coord->precipitation
  [im x y]
  (log/debugf "Getting precipitation for [%s, %s] ..." x y)
  (-> (map-io/rgb im x y)
      ((fn [x] (log/trace "RGB pixel:" x) x))
      util/rgb-pixel->color-map
      ((fn [x] (log/trace "Color map:" x) x))
      precipitation-amount
      ((fn [x] (log/trace "Precipitation:" x) x))))

(defn precipitation->pixel
  [milyr]
  (-> milyr
      color
      util/color-map->rgb-pixel))
