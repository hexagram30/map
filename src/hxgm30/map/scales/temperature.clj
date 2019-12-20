(ns hxgm30.map.scales.temperature
  (:require
   [clojure.string :as string]
   [hxgm30.map.io :as map-io]
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
(def temperature-range (- temperature-max temperature-min))
(def temperature-mean (/ temperature-range 2))
(def temperature-file "ilunao/temperature-scale-hex3")
(def temperature (scales-util/read-scale-txt temperature-file))
(def temperature-colors
  (memoize
   (fn [] (scales-util/scale-colors-txt temperature))))
(def temperature-count (count (temperature-colors)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Scaled Contract   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol ScaledRange
  ;; convenience methods
  (get-min [this])
  (get-max [this])
  (get-range [this])
  (get-mean [this])
  ;; how points are distributed over the temp range
  (get-normalized-min [this])
  (get-normalized-max [this])
  (get-normalized-range [this])
  (get-ticks-per-range [this])
  (get-ticks [this])
  ;; the final goal: ranges of temps, where each range is a bucket for a
  ;; differing number of points
  (get-ranges [this])
  (lookup [this])
  (reverse-lookup [this])
  (check-limits [this kelvin])
  (find-range [this kelvin])
  (get-color [this kelvin])
  (print-colors [this] [this step])
  (temperature-amount [this color-map])
  (coord->temperature [this im x y])
  (temperature->pixel [this kelvin]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn -normalized-range
  [this]
  (- (get-normalized-max this)
     (get-normalized-min this)))

(defn -lookup
  [this]
  (->> (scales-util/lookup-grouping
        (temperature-colors) (get-ranges this) {:rev? true})
       (map vec)
       (into {})))

(defn -reverse-lookup
  [this]
  (->> (scales-util/lookup-grouping
        (temperature-colors) (get-ranges this) {:rev? true})
       (map (comp vec reverse))
       (into {})))

(defn -check-limits
  [this kelvin]
  (cond (< kelvin (get-min this))
        (get-min this)

        (> kelvin (get-max this))
        (get-max this)

        :else
        kelvin))

(defn -find-range
  [this kelvin]
  (scales-util/find-range (check-limits this kelvin) (get-ranges this)))

(defn -get-color
  [this kelvin]
  (get (lookup this) (find-range this kelvin)))

(defn -print-color
  [this kelvin]
  (println (str (format "%,-3d K (%,3d F): "
                        kelvin
                        (int (util/to-fahrenheit kelvin)))
                (util/color-map->ansi (get-color this kelvin)))))

(defn -print-colors
  ([this]
   (print-colors this 2))
  ([this step]
   (let [output (mapv (partial -print-color this)
                      (range (get-min this) (+ (get-max this) step) step))]
     (println (string/join output)))))

(defn -temperature-amount
  "Given an RGB color hashmap, return the corresponding temperature."
  [this color-map]
  (->> color-map
       (get (reverse-lookup this))
       ((fn [x] (log/trace "Temperature amount:" x) x))
       (util/mean)))

(defn -coord->temperature
  [this im x y]
  (log/debugf "Getting temperature for [%s, %s] ..." x y)
  (->> (map-io/rgb im x y)
       ((fn [x] (log/trace "RGB pixel:" x) x))
       util/rgb-pixel->color-map
       ((fn [x] (log/trace "Color map:" x) x))
       (temperature-amount this)))

(defn -temperature->pixel
  [this kelvin]
  (log/debugf "Getting pixel for temperature %s ..." kelvin)
  (->> kelvin
       ((fn [x] (log/trace "Temperate:" x) x))
       (get-color this)
       ((fn [x] (log/trace "Color map:" x) x))
       util/color-map->rgb-pixel))

(def common-behaviour
  {:get-min (fn [_] temperature-min)
   :get-max (fn [_] temperature-max)
   :get-range (fn [_] temperature-range)
   :get-mean (fn [_] temperature-mean)
   :get-normalized-range -normalized-range
   :lookup -lookup
   :reverse-lookup -reverse-lookup
   :check-limits -check-limits
   :find-range -find-range
   :get-color -get-color
   :print-colors -print-colors
   :temperature-amount -temperature-amount
   :coord->temperature -coord->temperature
   :temperature->pixel -temperature->pixel})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Linear Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord LinearTemperatureRange
  [normalized-min normalized-max])

(defn linear-ticks-per-range
  [this]
  (float (/ (- (get-max this) (get-min this))
            (dec temperature-count))))

(defn linear-ticks
  [this]
  (sort (vec (set (flatten (get-ranges this))))))

(defn linear-ranges
  [this]
  (scales-util/get-ranges
   (get-min this) (get-max this) (linear-ticks-per-range this)))

(def linear-range-behaviour
  (assoc common-behaviour
         :get-normalized-min (:get-min common-behaviour)
         :get-normalized-max (:get-max common-behaviour)
         :get-normalized-range (:get-range common-behaviour)
         :get-ticks-per-range linear-ticks-per-range
         :get-ticks linear-ticks
         :get-ranges linear-ranges))

(extend LinearTemperatureRange
        ScaledRange
        linear-range-behaviour)

(defn new-linear-range
  []
  (map->LinearTemperatureRange {:normalized-min temperature-min
                                :normalized-max temperature-max}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Sine-based Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord SineTemperatureRange
  [normalized-min normalized-max])

(defn sine-ticks-per-range
  [this]
  (float (/ (get-normalized-range this)
            (/ temperature-count 2))))

(defn sine-ticks
  [this]
  (conj
   (reverse
    (map #(* (Math/sin (- (get-normalized-range this) (* (get-ticks-per-range this) %))))
         (range temperature-count)))
   -1))

(defn sine->temp
  [this s]
  (+ (- (get-max this) (get-mean this))
     (* (get-mean this) s)))


(defn sine-normalized-ranges
  [this]
  (let [xs (get-ticks this)]
    (partition 2 (interleave (butlast xs) (rest xs)))))

(defn sine-ranges
  [this]
  (map (fn [[x y]]
         [(sine->temp this x) (sine->temp this y)])
       (sine-normalized-ranges this)))

(def sine-range-behaviour
  (assoc common-behaviour
    :get-normalized-min :normalized-min
    :get-normalized-max :normalized-max
    :get-normalized-range #(- (:normalized-max %) (:normalized-min %))
    :get-ticks-per-range sine-ticks-per-range
    :get-ticks sine-ticks
    :get-ranges sine-ranges))

(extend SineTemperatureRange
  ScaledRange
  sine-range-behaviour)

(defn new-sine-range
  []
  (map->SineTemperatureRange {:normalized-min (/ Math/PI -4)
                              :normalized-max (/ Math/PI 4)}))

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
