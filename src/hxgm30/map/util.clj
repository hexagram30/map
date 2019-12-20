(ns hxgm30.map.util
  (:require
    [clojure.string :as string]
    [clojure.walk :refer [postwalk]]
    [com.evocomputing.colors :as colors]
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
    (map #(new BigInteger (string/join %) 16)
         (partition 2 components))))

(defn hex->color-map
  ""
  [color]
  (let [components (hex->rgb color)]
    {:red (nth components 0)
     :green (nth components 1)
     :blue (nth components 2)}))

(def ansi-square "███")
(def ansi-rectangle "███████")

(defn color-map->ansi
  ([color-map]
    (color-map->ansi color-map ansi-rectangle))
  ([color-map text]
    (str \u001b "[38;2;"
         (format "%s;%s;%sm%s"
                 (:red color-map)
                 (:green color-map)
                 (:blue color-map)
                 text)
         \u001b "[0m")))

(defn hex->ansi
  ([hex]
    (hex->ansi hex ansi-square))
  ([hex text]
    (color-map->ansi (hex->color-map hex) text)))

(defn color-map->hex
  [color-map]
  (format "0x%02x%02x%02x"
          (:red color-map)
          (:green color-map)
          (:blue color-map)))

(defn color-map->bash-true-color
  [color-map]
  (format "printf \"\\x1b[38;2;%s;%s;%sm%s\\x1b[0m\\n\""
          (:red color-map)
          (:green color-map)
          (:blue color-map)
          ansi-square))

(defn color-map->rgb-pixel
  [color-map]
  (try
    (bit-or
      (bit-shift-left (int (:red color-map)) 16)
      (bit-shift-left (int (:green color-map)) 8)
      (int (:blue color-map)))
    (catch Exception ex
      (log/debug "Error with color-map:" color-map)
      (log/debug ex))))

(defn color-map->rgba-pixel
  [color-map]
  (bit-or
    (bit-shift-left (int (:alpha color-map)) 24)
    (color-map->rgb-pixel color-map)))

(defn hex->rgb-pixel
  [hex]
  (color-map->rgb-pixel (hex->color-map hex)))

(defn rgb-pixel->color-map
  [pixel]
  {:red (bit-and (bit-shift-right pixel 16) 0x000000ff)
   :green (bit-and (bit-shift-right pixel 8) 0x000000ff)
   :blue (bit-and pixel 0x000000ff)})

(defn rgb-pixel->hex
  [pixel]
  (color-map->hex (rgb-pixel->color-map pixel)))

(defn rgba-pixel->color-map
  [pixel]
  (assoc (rgb-pixel->color-map pixel)
         :alpha (bit-and (bit-shift-right pixel 24) 0x000000ff)))

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

(defn mean
  [values]
  (log/trace "Getting mean for" values)
  (cond (nil? values) 0
        (= 1 (count values)) (first values)
        :else (Math/round (/ (reduce + values) (count values)))))

(defn nearest
  "The `nums` parameter needs to be a sorted collection.

  From Chouser: https://groups.google.com/forum/#!topic/clojure/quEzEM_ndCY"
  [nums x]
  (let [greater (first (subseq nums >= x))
        smaller (first (rsubseq nums <= x))]
    (apply min-key #(Math/abs (- % x)) (remove nil? [greater smaller]))))

(defn make-matrix
  ([xs ys]
   (make-matrix xs ys vector))
  ([xs ys f]
   (mapv (fn [x] (mapv #(f x %) ys)) xs)))

(defn to-fahrenheit [k]
  (- (* k 1.8) 459.67))

(defn print-gimp-palette
  [name color-map-col]
  (println "GIMP Palette")
  (println (str "Name: " name))
  (println "Columns: 0")
  (println "#")
  (for [x color-map-col]
    (println (format "%s\t%s\t%s\tHistory Color"
                     (:red x) (:green x) (:blue x)))))

(defn print-hexs->gimp-palette
  [name hex-vals]
  ;; XXX we can extract this logic later for sorting colors by spectrum,
  ;;     if it's ever needed anywhere else
  (->> (map colors/create-color hex-vals)
       (sort-by :hsl)
       (map (fn [c]
             (let [x (:rgba c)]
              {:red (nth x 0)
               :green (nth x 1)
               :blue (nth x 2)})))
       (print-gimp-palette name))
  :ok)

(def clj-hex->html (comp #(str "#" %) #(subs % 2 8)))

(defn clj-hexs->html
  [hex-vals]
  (map clj-hex->html hex-vals))
