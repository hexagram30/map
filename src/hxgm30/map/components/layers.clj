(ns hxgm30.map.components.layers
  (:require
    [com.stuartsierra.component :as component]
    [hxgm30.map.components.config :as config]
    [hxgm30.map.io :as map-io]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Layers Component API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn altitude
  [system]
  (get-in system [:layers :maps :altitude]))

(defn biomes
  [system]
  (get-in system [:layers :maps :biomes]))

(defn ls
  [system]
  (get-in system [:layers :maps :ls]))

(defn lsi
  [system]
  (get-in system [:layers :maps :lsi]))

(defn maps
  [system]
  (get-in system [:layers :maps]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Layers [])

(defn start
  [this]
  (log/info "Starting map layers component ...")
  (log/debug "Started map layers component.")
  (assoc this :maps {:altitude (map-io/read-planet (config/altitude-map this))
                     :biomes (map-io/read-planet (config/biomes-map this))
                     :ls (map-io/read-planet (config/land-sea-map this))
                     :lsi (map-io/read-planet (config/land-sea-ice-map this))}))

(defn stop
  [this]
  (log/info "Stopping map layers component ...")
  (log/debug "Stopped map layers component.")
  (dissoc this :altitude :biomes :ls :lsi))

(def lifecycle-behaviour
  {:start start
   :stop stop})

(extend Layers
  component/Lifecycle
  lifecycle-behaviour)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Constructor   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn create-component
  ""
  []
  (map->Layers {}))
