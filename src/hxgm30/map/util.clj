(ns hxgm30.map.util
  (:require
    [hxgm30.map.components.config :as config]
    [taoensso.timbre :as log]))

(defn lat-degrees-per-pixel
  [system]
  (/ 1 (config/pixels-per-lat-degree system)))

(defn lon-degrees-per-pixel
  [row]
  (let [pixels (count (:data row))]
    (/ 360.0 pixels)))

(defn no-band-data?
  [pixel-bands]
  (zero? (->> (dissoc pixel-bands :coords)
              vals
              (map #(->> %
                         vals
                         (reduce +)))
              (reduce +))))

(defn no-data?
  [bands-coll]
  (->> bands-coll
       (map no-band-data?)
       (some false?)
       not))

(def some-data? (complement no-data?))

(defn ys
  ([system]
    (ys system 0))
  ([system y-start]
    (range y-start (config/y-pixels system))))

(defn xs
  ([system]
    (xs system 0))
  ([system x-start]
    (range x-start (config/x-pixels system))))

(defn color-map->hex
  [color-map]
  (format "0x%02x%02x%02x"
          (:red color-map)
          (:green color-map)
          (:blue color-map)))

(defn color-map->bash-true-color
  [color-map]
  (format "printf \"\\x1b[38;2;%s;%s;%sm██\\x1b[0m\\n\""
          (:red color-map)
          (:green color-map)
          (:blue color-map)))

(def normalize-longitude
  #(if (<= % 180)
     %
     (- % 360)))

(def normalize-latitude
  #(if (<= % 90)
     %
     (- % 180)))
