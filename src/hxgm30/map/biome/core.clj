(ns hxgm30.map.biome.core
  (:require
    [clojure.data.avl :as avl]
    [clojusc.system-manager.core :as system]
    [clojusc.twig :as logger]
    [hxgm30.map.biome.precipitation :as precipitation]
    [hxgm30.map.biome.temperature :as temperature]
    [hxgm30.map.components.core]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.scales.core :as scales]
    [hxgm30.map.scales.precipitation :as precip-scale]
    [hxgm30.map.scales.temperature :as temp-scale]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log])
  (:import
    (java.awt.image BufferedImage))
  (:gen-class))

(def biomes-colorspace-file "ilunao/biomes-colors")
(def biomes-file "ilunao/biomes")
(def biomes-colorspace-tiny-file "ilunao/biomes-colors-tiny")
(def biomes-tiny-file "ilunao/biomes-tiny")

(def gen-temp-step #(+ temperature/K (* (Math/pow 2 %) 1.5)))
(def gen-precip-step #(* (Math/pow 2 %) 62.5))
(def biome-temps (conj (map gen-temp-step (concat (range 7) [3.4 4.6 5.6]))
                       temperature/K))
(def biome-precips (map gen-precip-step (range 9)))
(def cols (count biome-precips))
(def rows (count biome-temps))
(def sorted-temps (apply avl/sorted-set biome-temps))
(def sorted-precips (apply avl/sorted-set biome-precips))

(def nearest-temp #(util/nearest sorted-temps %))
(def nearest-precip #(util/nearest sorted-precips %))

(def ts temperature/ts)
(def ps precipitation/ps)

(def biomes (map-io/read-edn "ilunao/biomes"))
(def biomes-indexed (map-indexed vector biomes))
(def index-lookup-by-name (->> biomes-indexed
                               (map #(vector (:name (second %)) (first %)))
                               (into {})))
(def index-lookup-by-color (->> biomes-indexed
                                (map #(vector (:color (second %)) (first %)))
                                (into {})))
(def biomes-matrix [
  [ 0  0  0  0  0  0  0  0  0]
  [ 1  2  3  4  4  4  4  4  4]
  [ 5  6  7  8  8  8  8  8  8]
  [ 9 14 27 32 37 37 37 37 37]
  [10 15 19 28 33 38 38 38 38]
  [11 16 20 24 29 34 39 39 39]
  [12 17 21 25 30 35 40 40 40]
  [13 18 22 23 26 31 36 41 41]
  [13 13 18 22 23 26 31 36 41]
  [13 13 13 18 22 23 26 31 36]
  [13 13 13 13 18 22 23 26 31]])
(def biomes-temp-indexed
  (partition 2 (interleave sorted-temps biomes-matrix)))

(defn biomes-precip-indexed
  [row]
  (partition 2 (interleave sorted-precips row)))

(def temp-precip->biome-index
  (into {} (mapcat (fn [[y row]]
                     (map (fn [[x elem]] [[x y] elem])
                          (biomes-precip-indexed row)))
                   biomes-temp-indexed)))

(defn nearest
  [mm-precip kelvin-temp]
  (nth biomes
       (get temp-precip->biome-index [(nearest-precip mm-precip)
                                      (nearest-temp kelvin-temp)])))

(defn prep-biome-image
  [im]
  (new BufferedImage
       (map-io/width im)
       (map-io/height im)
       BufferedImage/TYPE_BYTE_INDEXED
       (.getColorModel im)))

(defn get-data
  [temp-im precip-im [x y]]
  (let [temp (scales/coord->temperature ts temp-im x y)
        precip (scales/coord->precipitation ps precip-im x y)
        biome (nearest precip temp)
        rgb (util/hex->rgb-pixel (:color biome))
        color-map (util/rgb-pixel->color-map rgb)]
    (assoc biome
      :biome-index (get index-lookup-by-color (:color biome))
      :rgb rgb
      :color-map color-map
      :ansi (util/color-map->ansi color-map)
      :kelvin temp
      :precip precip
      :nearest-temp (nearest-temp temp)
      :nearest-precip (nearest-precip precip))))

(defn set-biome-pixel!
  "Read a pixel from a temperature image, get its RGB values, and lookup the
  temperature for that pixel. Do the same for precipitation. Convert each color
  to its respective temperature and precipitation value. Use these to perform a
  biome lookup, extract the color for that biome, and then add the pixel with
  the new biome data to the biome image."
  [temp-im precip-im biome-im [x y]]
  (let [biome-data (get-data temp-im precip-im [x y])]
    (log/debugf
      "Got precip, temp, and biome: [%s %s] -> [%s %s] = %s %s"
      (:precip biome-data)
      (:kelvin biome-data)
      (:nearest-precip biome-data)
      (:nearest-temp biome-data)
      (:ansi biome-data)
      (:name biome-data))
    (try
      (map-io/set-rgb biome-im x y (:rgb biome-data))
      (catch Exception ex
        (log/error ex)
        (log/errorf
          (str "At position [%s, %s] couldn't process "
               "precip, temp, or biome ([%s, %s] = %s)")
          x
          y
          (:precip biome-data)
          (:kelvin biome-data)
          (:color biome-data))))))

(defn create-image
  "This function reads pixel data for temperature and precipitation from two
  files, then creates a new file with colors derived from these and the biomes
  lookup data."
  ([]
   (create-image (temperature/read-adjusted-temperature)
                 (precipitation/read-precipitation)
                 (map-io/read-png biomes-colorspace-file)
                 biomes-file))
  ([temp-im precip-im biome-colors-im outfile]
   (let [biome-im (prep-biome-image biome-colors-im)
         x-max (map-io/width temp-im)
         y-max (map-io/height temp-im)]
     (doall
       (for [x (range x-max)
             y (range y-max)]
         (set-biome-pixel! temp-im precip-im biome-im [x y])))
     (map-io/write
       biome-im
       (format (str "resources/" map-io/png-format) outfile)))))

(defn create-tiny-image
  []
    (create-image (map-io/read-png temperature/temperature-tiny-file)
                  (map-io/read-png precipitation/precipitation-tiny-file)
                  (map-io/read-png biomes-colorspace-tiny-file)
                  biomes-tiny-file))

(defn print-colors-row
  [row]
  (doseq [elem row]
    (print (util/color-map->ansi
              (util/hex->color-map (:color (nth biomes elem)))
              util/ansi-square)))
  (println))

(defn print-colors-matrix
  []
  (doseq [row biomes-matrix]
    (print-colors-row row)))

(defn print-legend
  []
  (doseq [zone biomes]
    (print (util/color-map->ansi
            (util/hex->color-map (:color zone))
            util/ansi-rectangle))
    (println " " (:name zone))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   CLI Usage   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def cli-setup-options {
  :init 'hxgm30.map.components.core/cli
  :throw-errors true})

(defn -main
  "Example usage:

    $ lein biome regen image
    $ lein biome regen tiny-image
    $ lein show legend

  "
  [& [cmd & [subcmd & args]]]
  (logger/set-level! '[hxgm30] :error)
  (system/setup-manager cli-setup-options)
  (system/startup)
  (log/debug "Command:" cmd)
  (log/debug "Sub-command:" subcmd)
  (log/debug "Args:" args)
  (let [subcmd (keyword subcmd)]
    (case (keyword cmd)
      :regen (case subcmd
               :image (do
                        (log/info "Regenerating biome image ...")
                        (create-image))
               :tiny-image (do
                             (log/info "Regenerating tiny biome image ...")
                             (create-tiny-image))
               (log/errorf "Undefined subcommand '%s'" subcmd))
      :show (case subcmd
              :legend (print-legend)
              (log/errorf "Undefined subcommand '%s'" subcmd))
      (log/errorf "Undefined command '%s'" subcmd))))
