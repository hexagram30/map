(ns hxgm30.map.biome.temperature
  (:require
    [clojure.java.io :as io]
    [hxgm30.map.io :as map-io]))

(defn alt-adjust-average-temp
  "Subtract 5.6 °C/K for each 1,000 meters of elevation, or in this case, every
  1,406 strides."
  [kelvin-temp m-elevation]
  (- kelvin-temp (* 5.6 (/ m-elevation 1406.0))))

(def temperature-file "001-mercator-offset-temperature")
(def elev-temp-file "001-mercator-offset-elevation-temperature")

(defn read-temperature
  []
  (map-io/read-png temperature-file))

(defn adjusted-temperature-pixel
  "Read a pixel from an elevation image. Obtain the RGB values for the pixel
  and lookup the elevation for that pixel. Also read that same pixel from a
  temperature image, getting its RGB value. If the elevation is less than ~1,400
  strides, simply copy the temperature pixel to the adjusted image amd return.
  If the elevation at the pixel is over ~1,400 strides, get the temperature
  value for the temperature RGB value and then apply the formula above. Add the
  pixel with the adjusted temperature data to the adjusted image."
  []
  ;; [elev-img temp-img adjust-img [xcoor ycoord]]
  ;; XXX get pixkes
  ;; XXX get RGB color-maps
  ;; XXX get elevation
  ;; XXX get temperature
  ;; XXX if elevation less than 1400, copy to adjsuted
  ;; XXX if elevation greater or equal, get adjusted temp and add that to the
  ;;     adjsuted image
  )

(defn create-updated-temperature-file
  "This function reads pixel data for elevation and temperature from two files,
  then creates a new file with adjusted global average temperatures based upon
  the elevation at each pixel."
  []
  ;; XXX open files to read and get image objects
  ;; XXX create image object for adjusted temperature
  ;; XXX get the width/height for pixels to read/write
  ;; XXX create a for loop over xs and ys to process each pixel
  ;; XXX open file o write the adjsuted temperature data to, and write it
  )
