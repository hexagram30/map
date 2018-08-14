(ns hxgm30.map.components.config
  (:require
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

(defn planet-radius
  [system]
  (get-in (get-cfg system) [:geography :planet-radius]))

(defn planet-circumference
  [system]
  (* 2 Math/PI (planet-radius system)))

(defn planet-area
  [system]
  (* Math/PI (Math/pow (planet-radius system) 2)))

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
