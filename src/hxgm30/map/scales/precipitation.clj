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

;;(def precipitation-min 0) ; in mils/year
;;(def precipitation-max 640000) ; in mils/year, ~12,000 mil/week (~16,000 mm/yr)
(def precipitation-min 0) ; in mm/year
(def precipitation-max 16000) ; in mm/year
(def precipitation-file "ilunao/precipitation-scale-hex")
(def default-color-step-size 500)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Shared Precipitation Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Shared Precipitation Methods   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn print-color
  [this rate]
  ;;(println (str (format "%,-7d mils/year: " rate)
  (println (str (format "%,-6d mm/year: " rate)
                (util/color-map->ansi (common/get-color this rate)))))

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
  (->> (map-io/rgb im x y)
       ((fn [x] (log/trace "RGB pixel:" x) x))
       util/rgb-pixel->color-map
       ((fn [x] (log/trace "Color map:" x) x))
       (precipitation-amount this)
       ((fn [x] (log/trace "Precipitation:" x) x))))

(defn precipitation->pixel
  [this rate]
  (->> rate
       ((fn [x] (log/trace "Precipitation:" x) x))
       (common/get-color this)
       ((fn [x] (log/trace "Color map:" x) x))
       util/color-map->rgb-pixel))

(def precipitation-range-behaviour
  {:precipitation-amount precipitation-amount
   :coord->precipitation coord->precipitation
   :precipitation->pixel precipitation->pixel})

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
;;;   Exponential Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord ExponentialPrecipitationRange
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

(defn exponential-ticks-per-range
  [this]
  (float (/ (:normalized-range this)
            (:color-count this))))

(defn exponential-ticks
  [this]
  (map (fn [x] (Math/pow (* x (exponential-ticks-per-range this))
                         (:power this)))
       (range (inc (:color-count this)))))

(defn exponential-ranges
  [this]
  (let [xs (exponential-ticks this)]
    (partition 2 (interleave (butlast xs) (rest xs)))))

(def exponential-range-behaviour
  (assoc common/behaviour
    :get-normalized-min :normalized-min
    :get-normalized-max :normalized-max
    :get-normalized-range :normalized-range
    :get-ticks-per-range exponential-ticks-per-range
    :get-ticks exponential-ticks
    :get-ranges exponential-ranges
    :print-colors print-colors))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Reverse Exponential Ranges   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord ReverseExponentialPrecipitationRange
  [color-count
   colors
   max
   mean
   min
   normalized-max
   normalized-min
   normalized-range
   power
   range
   ranges])

(defn rev-exp-ticks-per-range
  "This is actually for normalized ticks."
  [this]
  (float (/ (:normalized-range this)
            (:color-count this))))

(defn rev-exp-ticks
  [this]
  (let [rev-ranges (reverse (map #(- (second %) (first %))
                                 (exponential-ranges this)))]
    (reduce #(concat %1 (vector (+ %2 (last %1)))) [0] rev-ranges)))

(defn rev-exp-ranges
  [this]
  (let [xs (rev-exp-ticks this)]
    (partition 2 (interleave (butlast xs) (rest xs)))))

(def rev-exp-range-behaviour
  (assoc common/behaviour
    :get-normalized-min :normalized-min
    :get-normalized-max :normalized-max
    :get-normalized-range :normalized-range
    ;;:get-ticks-per-range rev-exp-ticks-per-range
    :get-ticks-per-range exponential-ticks-per-range
    :get-ticks rev-exp-ticks
    :get-ranges rev-exp-ranges
    :print-colors print-colors))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Constructors   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn new-linear-range
  []
  (let [r1 (assoc (precipitation-range-data)
             :normalized-min precipitation-min
             :normalized-max precipitation-max)
        r2 (assoc r1 :normalized-range (common/normalized-range r1))]
    (map->LinearPrecipitationRange
     (assoc r2 :ranges (common/linear-ranges r2)))))

(defn new-exponential-range
  [[power & _]]
  (let [r1 (assoc (precipitation-range-data)
             :normalized-min (Math/sqrt precipitation-min)
             :normalized-max (Math/sqrt precipitation-max)
             :power power)
        r2 (assoc r1 :normalized-range (common/normalized-range r1))]
    (map->ExponentialPrecipitationRange
     (assoc r2 :ranges (exponential-ranges r2)))))

(defn new-reverse-exponential-range
  [[power & _]]
  (let [r1 (assoc (precipitation-range-data)
             :normalized-min (Math/sqrt precipitation-min)
             :normalized-max (Math/sqrt precipitation-max)
             :power power)
        r2 (assoc r1 :normalized-range (common/normalized-range r1))]
    (map->ReverseExponentialPrecipitationRange
     (assoc r2 :ranges (rev-exp-ranges r2)))))

(defn new-range
  [^Keyword type args]
  (case type
    :linear (new-linear-range)
    :exponential (new-exponential-range args)
    :reverse-exponential (new-reverse-exponential-range args)
    :unsupported-precipitation-range-type))
