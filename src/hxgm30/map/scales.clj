(ns hxgm30.map.scales
  (:require
    [clojure.java.io :as io]
    [clojure.string :as string]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.units :as units]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log])
  (:import
    (java.awt.image BufferedImage)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   General   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn read-scale-img
  "Image files are read from the top-down, so high elevations and high numbers
  will be first."
  [filename]
  (->> filename
       (format map-io/png-format)
       map-io/read-resource-image))

(defn read-scale-txt
  ""
  [filename]
  (->> filename
       (format map-io/palette-format)
       map-io/read-resource-text))

(defn scale-colors-img
  ""
  [^BufferedImage im]
  (let [middle (int (/ (map-io/width im) 2))]
    (map #(map-io/bands im middle %) (range (map-io/height im)))))

(defn scale-colors-txt
  ""
  [lines]
  (map util/hex->color-map lines))

(defn get-ranges
  ""
  [start stop step]
  (map #(vector % (+ % step))
       (range start stop step)))

(defn- -maybe-reverse
  [opts data]
  (if (:rev? opts)
    (reverse data)
    data))

(defn lookup-grouping
  ([x-colors x-ranges]
    (lookup-grouping x-colors x-ranges {}))
  ([x-colors x-ranges opts]
    (->> x-colors
         (-maybe-reverse opts)
         (interleave x-ranges)
         (partition 2))))

(defn find-range
  [item collection]
  (reduce (fn [_ [a b]] (when (<= a item b) (reduced [a b])))
          []
          collection))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Elevation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def elevation-min 0) ; in strides (~2 leagues or ~7 miles)
(def elevation-max units/highest-mountain) ; in strides (~2 leagues or ~7 miles)
(def elevation-file "001-mercator-elevation-scale-hex")
(def elevation (read-scale-txt elevation-file))
(def elevation-colors
  (memoize
    (fn [] (scale-colors-txt elevation))))

(def strides-per-grade (float (/ (- elevation-max elevation-min)
                                 (dec (count (elevation-colors))))))

(defn elevation-ranges
  ""
  []
  (get-ranges elevation-min elevation-max strides-per-grade))

(def elevation-lookup
  (memoize
    (fn []
      (->> (lookup-grouping (elevation-colors) (elevation-ranges))
           (map vec)
           (into {})))))

(def elevation-rev-lookup
  (memoize
    (fn []
      (->> (lookup-grouping (elevation-colors) (elevation-ranges))
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
  (find-range (elevation-check strides) (elevation-ranges)))

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
(def precipitation (read-scale-txt precipitation-file))
(def precipitation-colors
  (memoize
    (fn [] (scale-colors-txt precipitation))))

(def milyr-per-grade (float (/ (- precipitation-max precipitation-min)
                                  (dec (count (precipitation-colors))))))

(defn precipitation-ranges
  ""
  []
  (get-ranges precipitation-min precipitation-max milyr-per-grade))

(def precipitation-lookup
  (memoize
    (fn []
      (->> (lookup-grouping
             (precipitation-colors) (precipitation-ranges))
           (map vec)
           (into {})))))

(def precipitation-rev-lookup
  (memoize
    (fn []
      (->> (lookup-grouping
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
  (find-range (precipitation-check milyr) (precipitation-ranges)))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Temperature   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def temperature-min 246) ; in degrees C/K
(def temperature-max 316) ; in degrees C/K
(def temperature-file "001-temperature-scale-hex2")
(def temperature (read-scale-txt temperature-file))
(def temperature-colors
  (memoize
    (fn [] (scale-colors-txt temperature))))

(def degrees-per-grade (float (/ (- temperature-max temperature-min)
                                 (dec (count (temperature-colors))))))

(defn temperature-ranges
  ""
  []
  (get-ranges temperature-min temperature-max degrees-per-grade))

(def temperature-lookup
  (memoize
    (fn []
      (->> (lookup-grouping
             (temperature-colors) (temperature-ranges) {:rev? true})
           (map vec)
           (into {})))))

(def temperature-rev-lookup
  (memoize
    (fn []
      (->> (lookup-grouping
             (temperature-colors) (temperature-ranges) {:rev? true})
           (map (comp vec reverse))
           (into {})))))

(defn temperature-check
  [kelvin]
  (cond (< kelvin temperature-min)
        temperature-min

        (> kelvin temperature-max)
        temperature-max

        :else
        kelvin))

(defn find-temperature-range
  [kelvin]
  (find-range (temperature-check kelvin) (temperature-ranges)))

(defn temperature-color
  [kelvin]
  (get (temperature-lookup) (find-temperature-range kelvin)))

(defn temperature-amount
  "Given an RGB color hashmap, return the corresponding temperature."
  [color-map]
  (->> color-map
       (get (temperature-rev-lookup))
       ((fn [x] (log/trace "Temperature amount:" x) x))
       (util/mean)))

(defn print-temperature-colors
  ([]
    (print-temperature-colors temperature-min temperature-max 2))
  ([start stop step]
    (let [output (mapv #(println (str (format "%,-3d K (%,3d F): " % (int (util/to-fahrenheit %)))
                                      (util/color-map->ansi
                                        (temperature-color %))))
                        (range start (+ stop step) step))]
      (println (string/join output)))))

(defn coord->temperature
  [im x y]
  (log/debugf "Getting temperature for [%s, %s] ..." x y)
  (-> (map-io/rgb im x y)
      ((fn [x] (log/trace "RGB pixel:" x) x))
      util/rgb-pixel->color-map
      ((fn [x] (log/trace "Color map:" x) x))
      temperature-amount))

(defn temperature->pixel
  [kelvin]
  (log/debugf "Getting pixel for temperature %s ..." kelvin)
  (-> kelvin
      ((fn [x] (log/trace "Temperate:" x) x))
      temperature-color
      ((fn [x] (log/trace "Color map:" x) x))
      util/color-map->rgb-pixel))
