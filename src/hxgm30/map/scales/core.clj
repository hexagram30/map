(ns hxgm30.map.scales.core
  (:require
   [hxgm30.map.scales.precipitation :as precipitation]
   [hxgm30.map.scales.temperature :as temperature])
  (:import
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
  (check-limits [this kelvin])
  (find-range [this kelvin])
  (get-color [this kelvin])
  (print-colors [this] [this step]))

(defprotocol TemperatureRange
  (temperature-amount [this color-map])
  (coord->temperature [this im x y])
  (temperature->pixel [this kelvin]))

(defprotocol PrecipitationRange
  (precipitation-amount [this color-map])
  (coord->precipitation [this im x y])
  (precipitation->pixel [this kelvin]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Temperature Implementations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(extend LinearTemperatureRange
        TemperatureRange
        temperature/linear-range-behaviour)

(extend LinearTemperatureRange
        ScaledRange
        temperature/scaled-range-behaviour)

(extend SineTemperatureRange
        TemperatureRange
        temperature/sine-range-behaviour)

(extend SineTemperatureRange
        ScaledRange
        temperature/scaled-range-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Precipitation Implementations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; TBD

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   General Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn new-scale
  [type sub-type]
  (case type
        :temperature (temperature/new-range sub-type)
        :precipitation (precipitation/new-range sub-type)
        :unsupported-scale-type))