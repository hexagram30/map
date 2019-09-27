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

(def elevation-min 0) ; in strides (~2 leagues or ~7 miles)
(def elevation-max units/highest-mountain) ; in strides (~2 leagues or ~7 miles)
(def temperature-min 212) ; in degrees C/K
(def temperature-max 333) ; in degrees C/K
(def precipitation-min 0) ; in mils/year (~4500 mm/yr)
(def precipitation-max 177000) ; in mils/year, ~3400 mil/week (~4500 mm/yr)

(def elevation-file "001-mercator-elevation-scale-hex")
(def temperature-file "001-mercator-temperature-scale-hex")
(def precipitation-file "001-mercator-precipitation-scale-hex")

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

(def elevation (read-scale-txt elevation-file))
(def temperature (read-scale-txt temperature-file))
(def precipitation (read-scale-txt precipitation-file))

(def elevation-colors
  (memoize
    (fn [] (scale-colors-txt elevation))))

(def temperature-colors
  (memoize
    (fn [] (scale-colors-txt temperature))))

(def precipitation-colors
  (memoize
    (fn [] (scale-colors-txt precipitation))))

(def strides-per-grade (float (/ (- elevation-max elevation-min)
                                 (dec (count (elevation-colors))))))

(def degrees-per-grade (float (/ (- temperature-max temperature-min)
                                 (dec (count (temperature-colors))))))

(def milyr-per-grade (float (/ (- precipitation-max precipitation-min)
                                  (dec (count (precipitation-colors))))))

(defn get-ranges
  ""
  [start stop step]
  (map #(vector % (+ % step))
       (range start stop step)))

(defn elevation-ranges
  ""
  []
  (get-ranges elevation-min elevation-max strides-per-grade))

(defn temperature-ranges
  ""
  []
  (get-ranges temperature-min temperature-max degrees-per-grade))

(defn precipitation-ranges
  ""
  []
  (get-ranges precipitation-min precipitation-max milyr-per-grade))

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

(def precipitation-lookup
  (memoize
    (fn []
      (->> (lookup-grouping
             (precipitation-colors) (precipitation-ranges) {:rev? true})
           (map vec)
           (into {})))))

(def precipitation-rev-lookup
  (memoize
    (fn []
      (->> (lookup-grouping
             (precipitation-colors) (precipitation-ranges) {:rev? true})
           (map (comp vec reverse))
           (into {})))))

(defn find-range
  [item collection]
  (reduce (fn [_ [a b]] (when (<= a item b) (reduced [a b])))
          []
          collection))

(defn find-elevation-range
  [strides]
  (find-range strides (elevation-ranges)))

(defn find-temperature-range
  [kelvin]
  (find-range kelvin (temperature-ranges)))

(defn find-precipitation-range
  [milyr]
  (find-range milyr (precipitation-ranges)))

(defn elevation-color
  [strides]
  (get (elevation-lookup) (find-elevation-range strides)))

(defn elevation-amount
  "Given an RGB color hashmap, return the corresponding elevation."
  [color-map]
  (util/mean (get (elevation-rev-lookup) color-map)))

(defn temperature-color
  [kelvin]
  (get (temperature-lookup) (find-temperature-range kelvin)))

(defn temperature-amount
  "Given an RGB color hashmap, return the corresponding temperature."
  [color-map]
  (util/mean (get (temperature-rev-lookup) color-map)))

(defn precipitation-color
  [milyr]
  (get (precipitation-lookup) (find-precipitation-range milyr)))

(defn precipitation-amount
  "Given an RGB color hashmap, return the corresponding annual precipitation."
  [color-map]
  (util/mean (get (precipitation-rev-lookup) color-map)))

(defn print-elevation-colors
  ([]
    (print-elevation-colors elevation-min elevation-max 1000))
  ([start stop step]
    (let [output (mapv #(println (str (format "%,-6d strides : " %)
                                      (util/color-map->ansi
                                        (elevation-color %))))
                       (range start (+ stop step) step))]
      (println (string/join output)))))

(defn print-temperature-colors
  ([]
    (print-temperature-colors temperature-min temperature-max 3))
  ([start stop step]
    (let [output (mapv #(println (str (format "%,-3d K : " %)
                                      (util/color-map->ansi
                                        (temperature-color %))))
                        (range start (+ stop step) step))]
      (println (string/join output)))))

(defn print-precipitation-colors
  ([]
    (print-precipitation-colors precipitation-min precipitation-max 20000))
  ([start stop step]
    (let [output (mapv #(println (str (format "%,-7d mils/year : " %)
                                      (util/color-map->ansi
                                        (precipitation-color %))))
                       (range start (+ stop step) step))]
      (println (string/join output)))))

(defn coord->temperature
  [im x y]
  (-> (map-io/rgb im x y)
      util/rgb-pixel->color-map
      temperature-amount))

(defn coord->elevation
  [im x y]
  (-> (map-io/rgb im x y)
      util/rgb-pixel->color-map
      elevation-amount))

(defn coord->precipitation
  [im x y]
  (-> (map-io/rgb im x y)
      util/rgb-pixel->color-map
      precipitation-amount))

(defn temperature->pixel
  [kelvin]
  (-> kelvin
      temperature-color
      util/color-map->rgb-pixel))

(defn elevation->pixel
  [strides]
  (-> strides
      elevation-color
      util/color-map->rgb-pixel))

(defn precipitation->pixel
  [milyr]
  (-> milyr
      precipitation-color
      util/color-map->rgb-pixel))
