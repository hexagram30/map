(ns hxgm30.map.scales
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.scales.util :as scales-util]
    [hxgm30.map.units :as units]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log])
  (:refer-clojure :exclude [get-ranges]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Elevation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def elevation-min 0) ; in strides (~2 leagues or ~7 miles)
(def elevation-max units/highest-mountain) ; in strides (~2 leagues or ~7 miles)
(def elevation-file "001-mercator-elevation-scale-hex")
(def elevation (scales-util/read-scale-txt elevation-file))
(def elevation-colors
  (memoize
    (fn [] (scales-util/scale-colors-txt elevation))))

(def strides-per-grade (float (/ (- elevation-max elevation-min)
                                 (dec (count (elevation-colors))))))

(defn elevation-ranges
  ""
  []
  (scales-util/get-ranges elevation-min elevation-max strides-per-grade))

(def elevation-lookup
  (memoize
    (fn []
      (->> (scales-util/lookup-grouping (elevation-colors) (elevation-ranges))
           (map vec)
           (into {})))))

(def elevation-rev-lookup
  (memoize
    (fn []
      (->> (scales-util/lookup-grouping (elevation-colors) (elevation-ranges))
           (map (comp vec reverse))
           (into {})))))

(defn elevation-check
  [strides]
  (cond (< strides elevation-min)
        elevation-min

        (> strides elevation-max)
        elevation-max

        :else
        strides))

(defn find-elevation-range
  [strides]
  (scales-util/find-range (elevation-check strides) (elevation-ranges)))

(defn elevation-color
  [strides]
  (get (elevation-lookup) (find-elevation-range strides)))

(defn elevation-amount
  "Given an RGB color hashmap, return the corresponding elevation."
  [color-map]
  (->> color-map
       (get (elevation-rev-lookup))
       ((fn [x] (log/trace "Elevation amount:" x) x))
       (util/mean)))

(defn print-elevation-colors
  ([]
    (print-elevation-colors elevation-min elevation-max 1000))
  ([start stop step]
    (let [output (mapv #(println (str (format "%,-6d strides : " %)
                                      (util/color-map->ansi
                                        (elevation-color %))))
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
      elevation-color
      util/color-map->rgb-pixel))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Precipitation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def precipitation-min 0) ; in mils/year (~4500 mm/yr)
(def precipitation-max 640000) ; in mils/year, ~12,000 mil/week (~16,000 mm/yr)
(def precipitation-file "001-mercator-precipitation-scale-hex")
(def precipitation (scales-util/read-scale-txt precipitation-file))
(def precipitation-colors
  (memoize
    (fn [] (scales-util/scale-colors-txt precipitation))))

(def milyr-per-grade (float (/ (- precipitation-max precipitation-min)
                                  (dec (count (precipitation-colors))))))

(defn precipitation-ranges
  ""
  []
  (scales-util/get-ranges precipitation-min precipitation-max milyr-per-grade))

(def precipitation-lookup
  (memoize
    (fn []
      (->> (scales-util/lookup-grouping
             (precipitation-colors) (precipitation-ranges))
           (map vec)
           (into {})))))

(def precipitation-rev-lookup
  (memoize
    (fn []
      (->> (scales-util/lookup-grouping
             (precipitation-colors) (precipitation-ranges))
           (map (comp vec reverse))
           (into {})))))

(defn precipitation-check
  [milyr]
  (cond (< milyr precipitation-min)
        precipitation-min

        (> milyr precipitation-max)
        precipitation-max

        :else
        milyr))

(defn find-precipitation-range
  [milyr]
  (scales-util/find-range (precipitation-check milyr) (precipitation-ranges)))

(defn precipitation-color
  [milyr]
  (get (precipitation-lookup) (find-precipitation-range milyr)))

(defn precipitation-amount
  "Given an RGB color hashmap, return the corresponding annual precipitation."
  [color-map]
  (->> color-map
       (get (precipitation-rev-lookup))
       ((fn [x] (log/trace "Precipitation amount:" x) x))
       (util/mean)))

(defn print-precipitation-colors
  ([]
    (print-precipitation-colors precipitation-min precipitation-max 43000))
  ([start stop step]
    (let [output (mapv #(println (str (format "%,-7d mils/year : " %)
                                      (util/color-map->ansi
                                        (precipitation-color %))))
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
      precipitation-color
      util/color-map->rgb-pixel))
