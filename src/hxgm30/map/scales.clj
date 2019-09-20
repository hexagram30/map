(ns hxgm30.map.scales
  (:require
    [clojure.java.io :as io]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log])
  (:import
    (java.awt.image BufferedImage)))

(def min-elevation 0)
(def max-elevation 30000)
(def min-temperature 213)
(def max-temperature 333)

(defn read-scale
  ""
  [filename]
  (->> filename
       (format map-io/png-format)
       map-io/read-resource-image))

(defn scale-colors
  ""
  [^BufferedImage im]
  (let [middle (int (/ (map-io/width im) 2))]
    (map #(map-io/bands im middle %) (range (map-io/height im)))))

(defn elevation-colors
  ""
  []
  (-> "001-mercator-elevation-scale"
      read-scale
      scale-colors))

(defn temperature-colors
  ""
  []
  (-> "001-mercator-temperature-scale"
      read-scale
      scale-colors))

;; XXX get the number of meters covered in each gradation

;; XXX get the number of degrees Kelvin that are covered in each gradation

;; XXX add a function for getting the nearest gradation for a given elevation

;; XXX add a function for getting the nearest gradation for a given temperature
