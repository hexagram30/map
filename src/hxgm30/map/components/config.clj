(ns hxgm30.map.components.config
  (:require
    [clojure.set :as set]
    [com.stuartsierra.component :as component]
    [hxgm30.map.config :as config]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn build-config
  []
  (config/data))

(defn- get-cfg
  [system]
  (get-in system [:config :data]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Config Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Distance

(defn stride
  [system]
  (get-in (get-cfg system) [:distance :stride]))

(defn mile
  [system]
  (get-in (get-cfg system) [:distance :mile]))

;; Geography

(defn latitude-circles
  [system]
  (get-in (get-cfg system) [:geography :latitude-circles]))

(defn planet-radius
  [system]
  (get-in (get-cfg system) [:geography :planet-radius]))

(defn planet-circumference
  [system]
  (* 2 Math/PI (planet-radius system)))

(defn planet-area
  [system]
  (* Math/PI (Math/pow (planet-radius system) 2)))

(defn altitude-colors
  [system]
  (get-in (get-cfg system) [:geography :colors :altitudes]))

(defn altitude-lookup
  ([system]
    (set/map-invert (altitude-colors system)))
  ([system altitude-color]
    (->> altitude-color
         (get (altitude-lookup system))
         name
         (Integer/parseInt))))

(defn biome-colors
  [system]
  (get-in (get-cfg system) [:geography :colors :biomes]))

(defn biome-lookup
  ([system]
    (let [biomes (set/map-invert (biome-colors system))
          sets (filter (fn [[k v]] (set? k)) biomes)
          split-out (->> sets
                         (mapcat (fn [[k v]] (map #(vector % v) k)))
                         (into {}))]
      (merge (->> sets
                  (map first)
                  (cons biomes)
                  (apply dissoc))
             split-out)))
  ([system biome-color]
    (get (biome-lookup system) biome-color)))

(defn ice-color
  [system]
  (get-in (get-cfg system) [:geography :colors :ice]))

(defn ice?
  [system band-color]
  (let [ice (ice-color system)]
    (and
      (> 50 (Math/abs (- (:blue ice)
                         (:blue band-color))))
      (> 50 (Math/abs (- (:green ice)
                         (:green band-color))))
      (> 50 (Math/abs (- (:red ice)
                         (:red band-color)))))))

(defn land-color
  [system]
  (get-in (get-cfg system) [:geography :colors :land]))

(defn land?
  [system band-color]
  (let [land (land-color system)]
    (and
      (> 20 (Math/abs (- (:blue land)
                         (:blue band-color))))
      (> 60 (Math/abs (- (:green land)
                         (:green band-color))))
      (> 90 (Math/abs (- (:red land)
                         (:red band-color)))))))

(defn sea-color
  [system]
  (get-in (get-cfg system) [:geography :colors :sea]))

(defn sea?
  [system band-color]
  (let [sea (sea-color system)]
    (and
      (> 10 (Math/abs (- (:blue sea)
                         (:blue band-color))))
      (> 10 (Math/abs (- (:green sea)
                         (:green band-color))))
      (> 10 (Math/abs (- (:red sea)
                         (:red band-color)))))))
;; Logging

(defn log-level
  [system]
  (get-in (get-cfg system) [:logging :level]))

(defn log-nss
  [system]
  (get-in (get-cfg system) [:logging :nss]))

;; Maps

(defn x-pixels
  [system]
  (get-in (get-cfg system) [:maps :pixels :x]))

(defn y-pixels
  [system]
  (get-in (get-cfg system) [:maps :pixels :y]))

(defn map-size
  [system]
  [(x-pixels system) (y-pixels system)])

(defn meters-per-pixel
  [system]
  (/ (planet-circumference system) (x-pixels system)))

(defn km-per-pixel
  [system]
  (/ (meters-per-pixel system) 1000))

(defn miles-per-pixel
  [system]
  (/ (meters-per-pixel system) (mile system)))

(defn pixels-per-lat-degree
  [system]
  (/ (y-pixels system) 2.0 (latitude-circles system)))

(defn lat-minutes-per-pixel
  [system]
  (/ 60 (pixels-per-lat-degree system)))

(defn altitude-map
  [system]
  (get-in (get-cfg system) [:maps :altitude]))

(defn biomes-map
  [system]
  (get-in (get-cfg system) [:maps :biomes]))

(defn land-sea-map
  [system]
  (get-in (get-cfg system) [:maps :ls]))

(defn land-sea-ice-map
  [system]
  (get-in (get-cfg system) [:maps :lsi]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Config [data])

(defn start
  [this]
  (log/info "Starting config component ...")
  (log/debug "Using configuration:" (:data this))
  (log/debug "Started config component.")
  this)

(defn stop
  [this]
  (log/info "Stopping config component ...")
  (log/debug "Stopped config component.")
  (assoc this :data nil))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend Config
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  [cfg-data]
  (map->Config {:data cfg-data}))
