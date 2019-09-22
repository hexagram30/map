(ns hxgm30.map.util
  (:require
    [clojure.string :as string]
    [clojure.walk :refer [postwalk]]
    [hxgm30.map.components.config :as config]
    [taoensso.timbre :as log])
  (:import
    (java.awt.image BufferedImage)))

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

(defn hex->rgb
  ""
  [color]
  (let [components (last (string/split color #"#"))]
    (map #(new BigInteger (apply str %) 16)
         (partition 2 components))))

(defn hex->color-map
  ""
  [color]
  (let [components (hex->rgb color)]
    {:red (nth components 0)
     :green (nth components 1)
     :blue (nth components 2)}))

(defn color-map->ansi
  ([color-map]
    (color-map->ansi color-map "███████"))
  ([color-map text]
    (str \u001b "[38;2;"
         (format "%s;%s;%sm%s"
                 (:red color-map)
                 (:green color-map)
                 (:blue color-map)
                 text)
         \u001b "[0m")))

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
  "The expected inputs here are latitude values computed by reading a bitmap
  image from [0 0] (upper left) across, and then proceeding down the rows.

  As such, with the assumption that [0 0] is in the Northern hemisphere (and at
  the prime meridian), values from 0 through 90 shouldbe inverted so that they
  are returned as 90 through 0. Similarly, the values below the equator should
  be changed from 90 through 180 to 0 through -90."
  #(if (= % 90)
         0
         (- 90 %)))

(defn rand-point
  [max-x max-y]
  [(rand-int (inc max-x))
   (rand-int (inc max-y))])

(defn rand-points
  ([^BufferedImage image point-count]
    (rand-points (.getWidth image)
                 (.getHeight image)
                 point-count))
  ([width height point-count]
    (map (fn [_] (rand-point width height)) (range (inc point-count)))))

(defn- -round-nested
  [data]
  (if (or (float? data) (double? data))
    (Math/round data)
    data))

(def round-nested #(postwalk -round-nested %))
