(ns hxgm30.map.biome.temperature
  (:require
    [hxgm30.map.biome.elevation :as elevation]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.scales.core :as scales]
    [hxgm30.map.scales.elevation :as elev-scale]
    [taoensso.timbre :as log])
  (:import
    (java.awt.image BufferedImage)))

(def K 273.15)
(def temperature-zone-height 1406.0)

(defn alt-adjust-average-temp
  "Subtract 5.6 °C/K for each 1,000 meters of elevation, or in this case, every
  1,406 strides."
  [kelvin-temp m-elevation]
  (- kelvin-temp (* 5.6 (/ m-elevation temperature-zone-height))))

(def temperature-file "ilunao/temperature")
(def temperature-tiny-file "ilunao/temperature-tiny")
(def elev-temp-file "ilunao/elevation-temperature")
;;(def ts (scales/new-scale :temperature :linear))
(def ts (scales/new-scale :temperature :sine))
;;(def ts (scales/new-scale :temperature :tangent))
;;(def ts (scales/new-scale :temperature :atanh))
;;(def ts (scales/new-scale :temperature :catenary 4))

(defn read-temperature
  []
  (map-io/read-png temperature-file))

(defn read-temperature-tiny
  []
  (map-io/read-png temperature-tiny-file))

;;(defn read-adjusted-temperature
;;  []
;;  (map-io/read-png elev-temp-file))
(def read-adjusted-temperature read-temperature)

(defn prep-adjusted-elevation
  [im]
  (new BufferedImage
       (map-io/width im)
       (map-io/height im)
       BufferedImage/TYPE_BYTE_INDEXED
       (.getColorModel im)))

(defn adjusted-temperature-pixel!
  "Read a pixel from an elevation image. Obtain the RGB values for the pixel
  and lookup the elevation for that pixel. Also read that same pixel from a
  temperature image, getting its RGB value. If the elevation is less than ~1,400
  strides, simply copy the temperature pixel to the adjusted image amd return.
  If the elevation at the pixel is over ~1,400 strides, get the temperature
  value for the temperature RGB value and then apply the formula above. Add the
  pixel with the adjusted temperature data to the adjusted image."
  [temp-im elev-im adj-im [x y]]
  (let [temp-pixel (map-io/rgb temp-im x y)
        temp (scales/coord->temperature ts temp-im x y)
        elev (elev-scale/coord->elevation elev-im x y)]
    (try
      (if (< elev temperature-zone-height)
        (map-io/set-rgb adj-im x y temp-pixel)
        (let [adj-temp (alt-adjust-average-temp temp elev)
              new-temp-pixel (scales/temperature->pixel ts adj-temp)]
          (map-io/set-rgb adj-im x y new-temp-pixel)))
      (catch Exception ex
        (log/debugf
          "At position [%s, %s] couldn't process temp %s or elev %s ..."
          x
          y
          temp
          elev)
        (log/debug ex)
        (map-io/set-rgb adj-im x y temp-pixel)))))

(defn create-updated-temperature-file
  "This function reads pixel data for elevation and temperature from two files,
  then creates a new file with adjusted global average temperatures based upon
  the elevation at each pixel."
  []
  (let [temp-im (read-temperature)
        elev-im (elevation/read-elevation)
        adj-im (prep-adjusted-elevation temp-im)
        x-max (map-io/width temp-im)
        y-max (map-io/height temp-im)]
    (doall
      (for [x (range x-max)
            y (range y-max)]
        (adjusted-temperature-pixel! temp-im elev-im adj-im [x y])))
    (map-io/write
      adj-im
      (format (str "resources/" map-io/png-format) elev-temp-file))))
