(ns hxgm30.map.scales.core
  (:require
   [clojusc.system-manager.core :as system]
   [clojusc.twig :as logger]
   [hxgm30.map.components.core]
   [hxgm30.map.scales.precipitation :as precipitation]
   [hxgm30.map.scales.temperature :as temperature]
   [taoensso.timbre :as log])
  (:import
   (hxgm30.map.scales.precipitation ExponentialPrecipitationRange
                                    LinearPrecipitationRange
                                    ReverseExponentialPrecipitationRange)
   (hxgm30.map.scales.temperature CatenaryTemperatureRange
                                  LinearTemperatureRange
                                  SineTemperatureRange))
  (:gen-class))

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

(extend CatenaryTemperatureRange
        TemperatureRange temperature/temperature-range-behaviour
        ScaledRange temperature/catenary-range-behaviour)

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
        :temperature (temperature/new-range sub-type rest)
        :precipitation (precipitation/new-range sub-type rest)
        :unsupported-scale-type))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   CLI Usage   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def cli-setup-options {
                        :init 'hxgm30.map.components.core/cli
                        :throw-errors true})

(defn print-data
  [coll]
  (doall
  (for [row coll]
    (println row))))

(defn -main
  "Example usage:

    $ lein scale show legend temperature linear
    $ lein scale show legend temperature sine
    $ lein scale show legend precipitation exponential 4.5
    $ lein scale show ranges precipitation exponential 4.5
    $ lein scale show ticks precipitation exponential 4.5
    $ lein scale show ticks-per-range precipitation exponential 4.5

  "
  [cmd subcmd scale-type scale-subtype & [scale-param & args]]
  (logger/set-level! '[hxgm30] :error)
  (system/setup-manager cli-setup-options)
  (system/startup)
  (log/debug "Command:" cmd)
  (log/debug "Sub-command:" subcmd)
  (log/debug "Scale type:" scale-type)
  (log/debug "Scale subtype:" scale-subtype)
  (log/debug "Args:" args)
  (let [subcmd (keyword subcmd)
        scale (new-scale (keyword scale-type)
                         (keyword scale-subtype)
                         (if (nil? scale-param)
                           scale-param
                           (Float/parseFloat scale-param)))]
    (case (keyword cmd)
      :show (case subcmd
              :legend (print-colors scale)
              :ranges (print-data (get-ranges scale))
              :ticks (print-data (get-ticks scale))
              :ticks-per-range (print-data (get-ticks-per-range scale))
              (log/errorf "Undefined subcommand '%s'" subcmd))
      (log/errorf "Undefined command '%s'" subcmd))))
