(ns hxgm30.map.scales
  (:require
    [clojure.java.io :as io]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log])
  (:import
    (java.awt.image BufferedImage)))

(def elevation-min 0) ; in meters
(def elevation-max 30000) ; in meters
(def temperature-min 212) ; in degrees C/K
(def temperature-max 333) ; in degrees C/K
(def precipitation-min 0) ; in mm/year
(def precipitation-max 4500) ; in mm/year

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

(def meters-per-grade (float (/ (- elevation-max elevation-min)
                                (dec (count (elevation-colors))))))

(def degrees-per-grade (float (/ (- temperature-max temperature-min)
                                 (dec (count (temperature-colors))))))

(def mmyr-per-grade (float (/ (- precipitation-max precipitation-min)
                                 (dec (count (precipitation-colors))))))

(defn get-ranges
  ""
  [start stop step]
  (map #(vector % (+ % step))
       (range start stop step)))

(defn elevation-ranges
  ""
  []
  (get-ranges elevation-min elevation-max meters-per-grade))

(defn temperature-ranges
  ""
  []
  (get-ranges temperature-min temperature-max degrees-per-grade))

(defn precipitation-ranges
  ""
  []
  (get-ranges precipitation-min precipitation-max mmyr-per-grade))

(def elevation-lookup
  (memoize
    (fn []
      (->> (elevation-colors)
           (interleave (elevation-ranges))
           (partition 2)
           (map vec)
           (into {})))))

(def temperature-lookup
  (memoize
    (fn []
      (->> (temperature-colors)
           reverse
           (interleave (temperature-ranges))
           (partition 2)
           (map vec)
           (into {})))))

(def precipitation-lookup
  (memoize
    (fn []
      (->> (precipitation-colors)
           reverse
           (interleave (precipitation-ranges))
           (partition 2)
           (map vec)
           (into {})))))

(defn find-range
  [item collection]
  (reduce (fn [_ [a b]] (when (<= a item b) (reduced [a b])))
          []
          collection))

(defn find-elevation-range
  [meters]
  (find-range meters (elevation-ranges)))

(defn find-temperature-range
  [kelvin]
  (find-range kelvin (temperature-ranges)))

(defn find-precipitation-range
  [mmyr]
  (find-range mmyr (precipitation-ranges)))

(defn elevation-color
  [meters]
  (get (elevation-lookup) (find-elevation-range meters)))

(defn temperature-color
  [kelvin]
  (get (temperature-lookup) (find-temperature-range kelvin)))

(defn precipitation-color
  [mmyr]
  (get (precipitation-lookup) (find-precipitation-range mmyr)))

(defn print-elevation-colors
  ([]
    (print-elevation-colors elevation-min elevation-max 1000))
  ([start stop step]
    (mapv
      #(println (str (format "%,-6dm : " %)
                     (util/color-map->ansi (elevation-color %))))
      (range start (+ stop step) step))
    :ok))

(defn print-temperature-colors
  ([]
    (print-temperature-colors temperature-min temperature-max 100))
  ([start stop step]
    (mapv
      #(println (str (format "%,-3dK : " %)
                     (util/color-map->ansi (temperature-color %))))
      (range start (+ stop step) step))
    :ok))

(defn print-precipitation-colors
  ([]
    (print-precipitation-colors precipitation-min precipitation-max 3))
  ([start stop step]
    (mapv
      #(println (str (format "%,-12dmm/year : " %)
                     (util/color-map->ansi (precipitation-color %))))
      (range start (+ stop step) step))
    :ok))
