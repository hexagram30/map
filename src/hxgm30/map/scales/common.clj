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
            (dec (:color-count this)))))

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

;; XXX let's change this to operate on :ranges instead ... for each element of
;;     ranges, take the mean between start and end; we'll probably need to include
;;     an option for tick-count instead of step

;;(map #(+ (first %) (/ (- (second %) (first %)) 2)) (:ranges this))

(defn print-colors
  [this print-fn step]
  (let [output (mapv (partial print-fn this)
                     (range (:min this) (+ (:max this) step) step))]
    (println (string/join output))))
