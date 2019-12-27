(ns hxgm30.map.scales.temperature
  (:require
   [hxgm30.map.io :as map-io]
   [hxgm30.map.scales.common :as common]
   [hxgm30.map.scales.util :as scales-util]
   [hxgm30.map.util :as util]
   [taoensso.timbre :as log])
  (:import
   (clojure.lang Keyword)
   (org.apache.commons.math3.util FastMath))
  (:refer-clojure :exclude [get-ranges]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Constants   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def temperature-min 222) ; in degrees C/K
;;(def temperature-min 246) ; in degrees C/K
;;(def temperature-min 252) ; in degrees C/K
;;(def temperature-max 316) ; in degrees C/K
(def temperature-max 306) ; in degrees C/K
(def temperature-file "ilunao/temperature-scale-hex3")
(def default-color-step-size 2)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Shared Temperature Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn temperature-colors
  []
  (scales-util/scale-colors-txt
   (scales-util/read-scale-txt temperature-file)))

(defn temperature-range-data
  []
  (let [colors (temperature-colors)
        r (- temperature-max temperature-min)]
    {:min temperature-min
     :colors colors
     :color-count (count colors)
     :max temperature-max
     :mean (+ (/ r 2) temperature-min)
     :normalized-min temperature-min
     :normalized-max temperature-max
     :range r}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Shared Temperature Methods   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn print-color
  [this kelvin]
  (println (str (format "%,-3d K (%,3d F): "
                        kelvin
                        (int (util/to-fahrenheit kelvin)))
                (util/color-map->ansi (common/get-color this kelvin)))))

(defn print-colors
  ([this]
   (print-colors this default-color-step-size))
  ([this step]
   (common/print-colors this print-color step)))

(defn temperature-amount
  "Given an RGB color hashmap, return the corresponding temperature."
  [this color-map]
  (->> color-map
       (get (common/reverse-lookup this))
       ((fn [x] (log/trace "Temperature amount:" x) x))
       (util/mean)))

(defn coord->temperature
  [this im x y]
  (log/debugf "Getting temperature for [%s, %s] ..." x y)
  (->> (map-io/rgb im x y)
       ((fn [x] (log/trace "RGB pixel:" x) x))
       util/rgb-pixel->color-map
       ((fn [x] (log/trace "Color map:" x) x))
       (temperature-amount this)))

(defn temperature->pixel
  [this kelvin]
  (log/debugf "Getting pixel for temperature %s ..." kelvin)
  (->> kelvin
       ((fn [x] (log/trace "Temperature:" x) x))
       (common/get-color this)
       ((fn [x] (log/trace "Color map:" x) x))
       util/color-map->rgb-pixel))

(def temperature-range-behaviour
  {:temperature-amount temperature-amount
   :coord->temperature coord->temperature
   :temperature->pixel temperature->pixel})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Linear Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord LinearTemperatureRange
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

(defn new-linear-range
  []
  (let [r1 (map->LinearTemperatureRange (assoc
                                        (temperature-range-data)
                                         :normalized-min temperature-min
                                         :normalized-max temperature-max))
        r2 (assoc r1 :normalized-range (common/normalized-range r1))]
    (assoc r2 :ranges (common/linear-ranges r2))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Sine-based Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord SineTemperatureRange
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

(defn sine-ticks-per-range
  [this]
  (float (/ (:normalized-range this)
            (/ (:color-count this) 2))))

(defn sine-normalized-ticks
  [this]
  (conj
   (reverse
    (map #(* (Math/sin (- (:normalized-range this)
                          (* (sine-ticks-per-range this) %))))
         (range (:color-count this))))
   -1))

(defn sine-ticks
  [this]
  (map #(+ (:min this) (* (:range this) (/ (+ 1 %) 2)))
       (sine-normalized-ticks this)))

(defn sine-ranges
  [this]
  (let [xs (sine-ticks this)]
    (partition 2 (interleave (butlast xs) (rest xs)))))

(def sine-range-behaviour
  (assoc common/behaviour
    :get-normalized-min :normalized-min
    :get-normalized-max :normalized-max
    :get-normalized-range :normalized-range
    :get-ticks-per-range sine-ticks-per-range
    :get-ticks sine-ticks
    :get-ranges sine-ranges
    :print-colors print-colors))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Tangent-based Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord TangentTemperatureRange
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

(defn tan-ticks-per-range
  [this]
  (float (/ (:normalized-range this)
            (:color-count this))))

(defn tan-inputs
  "These are the 'x' values that generate the corresponding 'y' values of the
  'tan-normalized-ticks' function."
  [this]
  (map #(+ (:normalized-min this) (* % (tan-ticks-per-range this)))
       (range (inc (:color-count this)))))

(defn tan-normalized-ticks
  [this]
  (map #(Math/tan %)
       (tan-inputs this)))

(defn tan-ticks
  [this]
  (let [norm-ticks (tan-normalized-ticks this)
        max-tick (apply max norm-ticks)
        min-tick (apply min norm-ticks)]
    (map #(+ (:min this) (* (:range this) (/ (+ (Math/abs min-tick) %) max-tick 2)))
         norm-ticks)))

(defn tan-ranges
  [this]
  (let [xs (tan-ticks this)]
    (partition 2 (interleave (butlast xs) (rest xs)))))

(def tan-range-behaviour
  (assoc common/behaviour
    :get-normalized-min :normalized-min
    :get-normalized-max :normalized-max
    :get-normalized-range :normalized-range
    :get-ticks-per-range tan-ticks-per-range
    :get-ticks tan-ticks
    :get-ranges tan-ranges
    :print-colors print-colors))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Inverse-Hyperbolic-Tangent-based Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord InverseHyperbolicTangentTemperatureRange
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

(defn atanh-ticks-per-range
  [this]
  (float (/ (:normalized-range this)
            (:color-count this))))

(defn atanh-inputs
  "These are the 'x' values that generate the corresponding 'y' values of the
  'atanh-normalized-ticks' function."
  [this]
  (map #(+ (:normalized-min this) (* % (atanh-ticks-per-range this)))
       (range (inc (:color-count this)))))

(defn atanh-normalized-ticks
  [this]
  (map #(FastMath/atanh %)
       (atanh-inputs this)))

(defn atanh-ticks
  [this]
  (let [norm-ticks (atanh-normalized-ticks this)
        max-tick (apply max norm-ticks)
        min-tick (apply min norm-ticks)]
    (map #(+ (:min this) (* (:range this) (/ (+ (Math/abs min-tick) %) max-tick 2)))
         norm-ticks)))

(defn atanh-ranges
  [this]
  (let [xs (atanh-ticks this)]
    (partition 2 (interleave (butlast xs) (rest xs)))))

(def atanh-range-behaviour
  (assoc common/behaviour
    :get-normalized-min :normalized-min
    :get-normalized-max :normalized-max
    :get-normalized-range :normalized-range
    :get-ticks-per-range atanh-ticks-per-range
    :get-ticks atanh-ticks
    :get-ranges atanh-ranges
    :print-colors print-colors))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Catenary Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord CatenaryTemperatureRange
  [color-count
   colors
   curvature
   max
   mean
   min
   normalized-max
   normalized-min
   normalized-range
   range
   ranges])

(defn catenary-ticks-per-range
  [this]
  (float (/ (* 2 (:curvature this))
            (:color-count this))))

(defn catenary-half-range
  [this sign]
  (map #(* sign % (catenary-ticks-per-range this))
       (rest (range (inc (/ (:color-count this) 2))))))

(defn catenary-inputs
  "These are the 'x' values that generate the corresponding 'y' values of the
  'catenary-ticks' function."
  [this]
  (concat (reverse (catenary-half-range this -1))
          [0]
          (catenary-half-range this 1)))

(defn catenary-normalized-ticks
  [this]
  (map #(* (Math/cosh %)
           (if (neg? %) -1 1))
       (catenary-inputs this)))

(defn catenary-adjusted-ticks
  [this]
  (map #(/ %
           (/ (common/normalized-range this) (:range this)))
       (catenary-normalized-ticks this)))

(defn catenary-ticks
  "This returns the 'y' values for the given scale, one for each color count."
  [this]
  (let [adj-ticks (catenary-adjusted-ticks this)]
    (map #(+ (apply max adj-ticks)
             (:min this)
             %)
         adj-ticks)))

(defn catenary-ranges
  [this]
  (let [ys (catenary-ticks this)]
    (partition 2 (interleave (butlast ys) (rest ys)))))

(def catenary-range-behaviour
  (assoc common/behaviour
    :get-normalized-min :normalized-min
    :get-normalized-max :normalized-max
    :get-normalized-range :normalized-range
    :get-ticks-per-range catenary-ticks-per-range
    :get-ticks catenary-ticks
    :get-ranges catenary-ranges
    :print-colors print-colors))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Constructors   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn new-linear-range
  []
  (let [r1 (assoc (temperature-range-data)
             :normalized-min temperature-min
             :normalized-max temperature-max)
        r2 (assoc r1 :normalized-range (common/normalized-range r1))]
    (map->LinearTemperatureRange
     (assoc r2 :ranges (common/linear-ranges r2)))))

(defn new-sine-range
  []
  (let [r1 (assoc (temperature-range-data)
             :normalized-min (/ Math/PI -4)
             :normalized-max (/ Math/PI 4))
        r2 (assoc r1 :normalized-range (common/normalized-range r1))]
    (map->SineTemperatureRange
     (assoc r2 :ranges (sine-ranges r2)))))

(defn new-tangent-range
  []
  (let [r1 (assoc (temperature-range-data)
             :normalized-min -0.5
             :normalized-max 0.5)
        r2 (assoc r1 :normalized-range (common/normalized-range r1))]
    (map->TangentTemperatureRange
     (assoc r2 :ranges (tan-ranges r2)))))

(defn new-atanh-range
  []
  (let [r1 (assoc (temperature-range-data)
             :normalized-min -0.9
             :normalized-max 0.9)
        r2 (assoc r1 :normalized-range (common/normalized-range r1))]
    (map->InverseHyperbolicTangentTemperatureRange
     (assoc r2 :ranges (atanh-ranges r2)))))

(defn new-catenary-range
  [[curvature & _]]
  (let [r1 (assoc (temperature-range-data) :curvature curvature)
        ticks (catenary-normalized-ticks r1)
        r2 (assoc r1 :normalized-max (apply max ticks)
                     :normalized-min (apply min ticks))
        r3 (assoc r2 :normalized-range (common/normalized-range r2))]
    (map->CatenaryTemperatureRange
     (assoc r3 :ranges (catenary-ranges r3)))))

(defn new-range
  [^Keyword type args]
  (case type
    :atanh (new-atanh-range)
    :linear (new-linear-range)
    :sine (new-sine-range)
    :tangent (new-tangent-range)
    :catenary (new-catenary-range args)
    :unsupported-temperature-range-type))

(comment
 (temperature-ranges)
 (map (fn [[x1 x2]]
        (Math/abs (- x2 x1)))
      (partition 2 (interleave (butlast xs) (rest xs))))
 (map (fn [[x1 x2]]
        (Math/abs (- x2 x1)))
      (temperature-ranges))
 )
