(ns hxgm30.map.io
  (:require
    [clojure.java.io :as io])
  (:import
    (clojure.lang Keyword)
    (java.awt.image BufferedImage)
    (javax.imageio ImageIO)
    (sun.awt.image ByteInterleavedRaster)))

(def file-format
  "planets/%s.bmp")

(defn read-planet
  "Given a string value for a planet's .bmp file (without the .bmp extension)
  read the file and load the image data. Note that ImageIO returns a
  `java.awt.image.BufferedImage`."
  [file-name]
  (->> file-name
       (format file-format)
       io/resource
       (ImageIO/read)))

(defprotocol SizedAPI
  (height [this])
  (width [this]))

(def sized-behaviour
  {:height #(.getHeight %)
   :width #(.getWidth %)})

(extend BufferedImage
        SizedAPI
        sized-behaviour)

(extend ByteInterleavedRaster
        SizedAPI
        sized-behaviour)

;; Note: BufferedImage starts counting [0,0] at the upper-left of the image.
(defprotocol BmpAPI
  (band [this band-key x y] (str "Extract the value for the given band at the given "
                                 "`x` and `y` coordinate. Valid values for band are "
                                 "`:red`, `:green`, or `:blue`."))
  (band-percent [this band-key x y] (str "The same as the `band` function, except that "
                                         "the values returned are between 0 and 100."))
  (data [this] "Returns the image as one large tile.")
  (min-tile-x [this] "Returns the minimum tile index in the x direction.")
  (min-tile-y [this] "Returns the minimum tile index in the y direction.")
  (min-x [this] "Returns the minimum x coordinate of this `BufferedImage`.")
  (min-y [this] "Returns the minimum y coordinate of this `BufferedImage`.")
  (rgb [this x y] (str "Returns an integer pixel in the default RGB color model "
                       "(TYPE_INT_ARGB) and default sRGB colorspace."))
  (tile [this x y])
  (tile-height [this])
  (tile-width [this])
  (x-tiles-count [this] "Returns the number of tiles in the x direction.")
  (y-tiles-count [this] "Returns the number of tiles in the y direction."))

(defn bands
  [this x y]
  (let [values (rgb this x y)]
    {:red (bit-and (bit-shift-right values 16) 0x000000ff)
     :green (bit-and (bit-shift-right values 8) 0x000000ff)
     :blue (bit-and values 0x000000ff)}))

(defn band
  [this ^Keyword band-key x y]
  (let [values (rgb this x y)]
    (case band-key
      :red (bit-and (bit-shift-right values 16) 0x000000ff)
      :green (bit-and (bit-shift-right values 8) 0x000000ff)
      :blue (bit-and values 0x000000ff))))

(def bmp-behaviour
  {:band band
   :band-percent #(* (/ (band %1 %2 %3 %4) 255.0) 100)
   :data #(.getData %)
   :min-tile-x #(.getMinTileX %)
   :min-tile-y #(.getMinTileY %)
   :min-x #(.getMinX %)
   :min-y #(.getMinY %)
   :rgb #(.getRGB %1 %2 %3)
   :tile #(.getTile %1 %2 %3)
   :tile-height #(.getTileHeight %)
   :tile-width #(.getTileWidth %)
   :x-tiles-count #(.getNumXTiles %)
   :y-tiles-count #(.getNumYTiles %)})

(extend BufferedImage
        BmpAPI
        bmp-behaviour)

(defprotocol BmpRasterAPI
  (bounds [this] "Returns the bounding Rectangle of this Raster.")
  (band-count [this] "Returns the number of bands (samples per pixel) in this Raster.")
  (pixel [this x y])
  (pixels [this x y w h]))

(def raster-behaviour
  {:bounds #(.getHeight %)
   :band-count #(.getWidth %)
   :pixel #(.getData %1 %2 %3)
   :pixels #(.getTile %1 %2 %3 %4 %5)})

(extend ByteInterleavedRaster
        BmpRasterAPI
        raster-behaviour)
