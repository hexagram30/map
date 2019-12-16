(ns hxgm30.map.scales.util
  (:require
   [clojure.java.io :as io]
   [clojure.string :as string]
   [hxgm30.map.io :as map-io]
   [hxgm30.map.units :as units]
   [hxgm30.map.util :as util]
   [taoensso.timbre :as log])
  (:import
   (java.awt.image BufferedImage))
  (:refer-clojure :exclude [get-ranges]))

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