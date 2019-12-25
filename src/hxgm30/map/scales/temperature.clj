(ns hxgm30.map.scales.temperature
  (:require
   [hxgm30.map.io :as map-io]
   [hxgm30.map.scales.common :as common]
   [hxgm30.map.scales.util :as scales-util]
   [hxgm30.map.util :as util]
   [taoensso.timbre :as log])
  (:import
   (clojure.lang Keyword))
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

(defn sine-ticks
  [this]
  (conj
   (reverse
    (map #(* (Math/sin (- (:normalized-range this)
                          (* (sine-ticks-per-range this) %))))
         (range (:color-count this))))
   -1))

(defn sine->temp
  [this s]
  (+ (- (:max this) (:mean this))
     (* (:mean this) s)))

(defn sine-normalized-ranges
  [this]
  (let [xs (sine-ticks this)]
    (partition 2 (interleave (butlast xs) (rest xs)))))

(defn sine-ranges
  [this]
  (map (fn [[x y]]
         [(sine->temp this x) (sine->temp this y)])
       (sine-normalized-ranges this)))

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

;;(defrecord TanTemperatureRange
;;  [normalized-min normalized-max]
;;  ScaledRange
;;  )
;;
;;(defn new-tan-temp-range
;;  []
;;  (map->TanTemperatureRange {
;;                             :normalized-min (* Math/PI -0.2)
;;                             :normalized-max (* Math/PI 0.2)}))
;;
;;(def min-distrib (* Math/PI -0.2))
;;(def max-distrib (* Math/PI 0.2))
;;(def distrib-interval (- max-distrib min-distrib))
;;(def ticks-per-range (/ distrib-interval (inc (/ temperature-count 2))))
;;(def distrib-range (* (inc temperature-count) ticks-per-range))
;;(def distrib-mean (/ distrib-range 2))
;;(def xs
;;  (rest
;;   (map #(Math/tan (- distrib-mean (* ticks-per-range %)))
;;        (range (inc temperature-count)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Inverse-Hyperbolic-Tangent-based Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;(defrecord AtanhTemperatureRange
;;  [normalized-min normalized-max]
;;  ScaledRange
;;  )
;;
;;(defn new-atanh-temp-range
;;  []
;;  (map->AtanhTemperatureRange {
;;                                :normalized-min -1
;;                                :normalized-max 1}))
;;
;;(def min-distrib -1)
;;(def max-distrib 1)
;;(def distrib-interval (- max-distrib min-distrib))
;;(def ticks-per-range (/ distrib-interval (inc (/ temperature-count 2.0))))
;;(def distrib-range (* (inc temperature-count) ticks-per-range))
;;(def distrib-mean (/ distrib-range 2))
;;(def xs
;;  (rest
;;   (map #(FastMath/atanh (- distrib-mean (* ticks-per-range %)))
;;        (range (inc temperature-count)))))



;;(defn temperature-ranges
;;  []
;;  (reverse
;;   (map (fn [range]
;;          (reverse
;;           (map #(+ (- temperature-max temperature-mean)
;;                    (* temperature-mean %)) range)))
;;        (partition 2 (interleave (butlast xs) (rest xs))))))

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

(defn new-range
  [^Keyword type]
  (case type
    :linear (new-linear-range)
    :sine (new-sine-range)
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
