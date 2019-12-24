(ns hxgm30.map.scales.core
  (:require
   [hxgm30.map.scales.precipitation :as precipitation]
   [hxgm30.map.scales.temperature :as temperature])
  (:import
   (hxgm30.map.scales.precipitation ExponentialPrecipitationRange
                                    LinearPrecipitationRange
                                    ReverseExponentialPrecipitationRange)
   (hxgm30.map.scales.temperature LinearTemperatureRange
                                  SineTemperatureRange)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Protocols   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
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
  (check-limits [this value])
  (find-range [this value])
  (get-color [this value])
  (print-colors [this] [this step]))

(defprotocol TemperatureRange
  (temperature-amount [this color-map])
  (coord->temperature [this im x y])
  (temperature->pixel [this kelvin]))

(defprotocol PrecipitationRange
  (precipitation-amount [this color-map])
  (coord->precipitation [this im x y])
  (precipitation->pixel [this rate]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Temperature Implementations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(extend LinearTemperatureRange
  TemperatureRange temperature/temperature-range-behaviour
  ScaledRange temperature/linear-range-behaviour)

(extend SineTemperatureRange
  TemperatureRange temperature/temperature-range-behaviour
  ScaledRange temperature/sine-range-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Precipitation Implementations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(extend LinearPrecipitationRange
  PrecipitationRange precipitation/precipitation-range-behaviour
  ScaledRange precipitation/linear-range-behaviour)

(extend ExponentialPrecipitationRange
  PrecipitationRange precipitation/precipitation-range-behaviour
  ScaledRange precipitation/exponential-range-behaviour)

(extend ReverseExponentialPrecipitationRange
        PrecipitationRange precipitation/precipitation-range-behaviour
        ScaledRange precipitation/rev-exp-range-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   General Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn new-scale
  [type sub-type & rest]
  (case type
        :temperature (temperature/new-range sub-type)
        :precipitation (precipitation/new-range sub-type rest)
        :unsupported-scale-type))
