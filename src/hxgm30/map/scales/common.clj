(ns hxgm30.map.scales.common
  (:require
   [clojure.string :as string]
   [hxgm30.map.scales.util :as scales-util])
  (:refer-clojure :exclude [get-ranges]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common Linear   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn linear-ticks-per-range
  [this]
  (float (/ (- (:max this) (:min this))
            (:color-count this))))

(defn linear-ranges
  [this]
  (scales-util/get-ranges
   (:min this) (:max this) (linear-ticks-per-range this)))

(defn linear-ticks
  [this]
  (sort (vec (set (flatten (linear-ranges this))))))

(def linear-behaviour
  {:get-ticks-per-range linear-ticks-per-range
   :get-ticks linear-ticks
   :get-ranges linear-ranges})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn normalized-range
  [this]
  (- (:normalized-max this)
     (:normalized-min this)))

(defn lookup
  [this]
  (->> (scales-util/lookup-grouping
        (:colors this) (:ranges this) {:rev? true})
       (map vec)
       (into {})))

(defn reverse-lookup
  [this]
  (->> (scales-util/lookup-grouping
        (:colors this) (:ranges this) {:rev? true})
       (map (comp vec reverse))
       (into {})))

(defn check-limits
  [this value]
  (cond (< value (:min this))
        (:min this)

        (> value (:max this))
        (:max this)

        :else
        value))

(defn find-range
  [this value]
  (scales-util/find-range (check-limits this value) (:ranges this)))

(defn get-color
  [this value]
  (get (lookup this) (find-range this value)))

(def behaviour
  {:get-min :min
   :get-max :max
   :get-range :range
   :get-mean :mean
   :get-normalized-range normalized-range
   :get-colors :colors
   :get-color-count :color-count
   :lookup lookup
   :reverse-lookup reverse-lookup
   :check-limits check-limits
   :find-range find-range
   :get-color get-color})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-midpoint
  [[v1 v2]]
  (+ (min v1 v2) (/ (Math/abs (- v2 v1)) 2)))

(defn get-midpoints
  ([this]
   (get-midpoints this (:ranges this)))
  ([_ points]
   (map get-midpoint points)))

(defn print-colors
  [this print-fn count]
  ;; XXX add support for fewer or greater numbers of rows to display
  (let [output (mapv (partial print-fn this) (get-midpoints this))]
    (println (string/join output))))
