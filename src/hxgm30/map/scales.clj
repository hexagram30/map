(ns hxgm30.map.scales
  (:require
    [clojure.java.io :as io]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log])
  (:import
    (java.awt.image BufferedImage)))

(def min-elevation 0) ; in meters
(def max-elevation 30000) ; in meters
(def min-temperature 212) ; in degrees C/K
(def max-temperature 333) ; in degrees C/K
(def min-precipitation 0) ; in mm/year
(def max-precipitation 4500) ; in mm/year

(def elevation-file "001-mercator-elevation-scale")
(def temperature-file "001-mercator-temperature-scale")
(def precipitation-file "001-mercator-precipitation-scale")

(defn read-scale
  "Image files are read from the top-down, so high elevations and high numbers
  will be first."
  [filename]
  (->> filename
       (format map-io/png-format)
       map-io/read-resource-image))

(defn scale-colors
  ""
  [^BufferedImage im]
  (let [middle (int (/ (map-io/width im) 2))]
    (map #(map-io/bands im middle %) (range (map-io/height im)))))

(def elevation (read-scale elevation-file))
(def temperature (read-scale temperature-file))
(def precipitation (read-scale precipitation-file))

(def elevation-colors
  (memoize
    (fn [] (scale-colors elevation))))

(def temperature-colors
  (memoize
    (fn [] (scale-colors temperature))))

(def precipitation-colors
  (memoize
    (fn [] (scale-colors precipitation))))

(def meters-per-grade (float (/ (- max-elevation min-elevation)
                                (dec (map-io/height elevation)))))

(def degrees-per-grade (float (/ (- max-temperature min-temperature)
                                 (dec (map-io/height temperature)))))

(def mmyr-per-grade (float (/ (- max-precipitation min-precipitation)
                                 (dec (map-io/height precipitation)))))

(defn get-ranges
  ""
  [start stop step]
  (map #(vector % (+ % step))
       (range start stop step)))

(defn elevation-ranges
  ""
  []
  (get-ranges min-elevation max-elevation meters-per-grade))

(defn temperature-ranges
  ""
  []
  (get-ranges min-temperature max-temperature degrees-per-grade))

(defn precipitation-ranges
  ""
  []
  (get-ranges min-precipitation max-precipitation mmyr-per-grade))

(def elevation-lookup
  (memoize
    (fn []
      (->> (elevation-colors)
           reverse
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
