(ns hxgm30.map.scales.precipitation
  (:require
   [hxgm30.map.io :as map-io]
   [hxgm30.map.scales.common :as common]
   [hxgm30.map.scales.util :as scales-util]
   [hxgm30.map.util :as util]
   [taoensso.timbre :as log])
  (:import
   (clojure.lang Keyword))
  (:refer-clojure :exclude [get-ranges min max]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Constants   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def precipitation-min 0) ; in mils/year (~4500 mm/yr)
(def precipitation-max 640000) ; in mils/year, ~12,000 mil/week (~16,000 mm/yr)
(def precipitation-file "ilunao/precipitation-scale-hex")
(def default-color-step-size 43000)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Shared Precipitation Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn precipitation-colors
  []
  (scales-util/scale-colors-txt
   (scales-util/read-scale-txt precipitation-file)))

(defn precipitation-range-data
  []
  (let [colors (precipitation-colors)
        r (- precipitation-max precipitation-min)]
    {:min precipitation-min
     :colors colors
     :color-count (count colors)
     :max precipitation-max
     :mean (+ (/ r 2) precipitation-min)
     :normalized-min precipitation-min
     :normalized-max precipitation-max
     :range r}))

(defn print-color
  [this milyr]
  (println (str (format "%,-7d mils/year: " milyr)
                (util/color-map->ansi (common/get-color this milyr)))))

(defn print-colors
  ([this]
   (print-colors this default-color-step-size))
  ([this step]
   (common/print-colors this print-color step)))

  (defn precipitation-amount
    "Given an RGB color hashmap, return the corresponding annual precipitation."
    [this color-map]
    (->> color-map
         (get (common/reverse-lookup this))
         ((fn [x] (log/trace "Precipitation amount:" x) x))
         (util/mean)))

  (defn coord->precipitation
    [this im x y]
    (log/debugf "Getting precipitation for [%s, %s] ..." x y)
    (-> (map-io/rgb im x y)
        ((fn [x] (log/trace "RGB pixel:" x) x))
        util/rgb-pixel->color-map
        ((fn [x] (log/trace "Color map:" x) x))
        (precipitation-amount this)
        ((fn [x] (log/trace "Precipitation:" x) x))))

  (defn precipitation->pixel
    [this milyr]
    (-> milyr
        (common/get-color this)
        util/color-map->rgb-pixel))

  (def scaled-range-behaviour
    {:temperature-amount precipitation-amount
     :coord->temperature coord->precipitation
     :temperature->pixel precipitation->pixel})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Linear Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord LinearPrecipitationRange
  [color-count
   colors
   max
   mean
   min
   normalized-max
   normalized-min
   normalized-range
   range
   ranges])

(def linear-range-behaviour
  (assoc (merge common/behaviour common/linear-behaviour)
    :get-normalized-min :normalized-min
    :get-normalized-max :normalized-max
    :get-normalized-range :normalized-range
    :print-colors print-colors))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Constructors   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn new-linear-range
  []
  (let [r1 (map->LinearPrecipitationRange (assoc
                                          (precipitation-range-data)
                                           :normalized-min precipitation-min
                                           :normalized-max precipitation-max))
        r2 (assoc r1 :normalized-range (common/normalized-range r1))]
    (assoc r2 :ranges (common/linear-ranges r2))))

(defn new-range
  [^Keyword type]
  (case type
    :linear (new-linear-range)
    :unsupported-precipitation-range-type))