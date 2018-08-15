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

(defn ys
  ([system]
    (ys system 0))
  ([system y-start]
    (range y-start (config/y-pixels system))))

(defn xs
  ([system]
    (xs system 0))
  ([system x-start]
    (range x-start (config/x-pixels system))))

(defn layer-bands
  [system [x y :as coords]]
  (into {:coords coords} (map (fn [[k v]]
                           [k (map-io/bands v x y)])
                         (maps system))))
(defn no-band-data?
  [band]
  (zero? (->> (dissoc band :coords)
              vals
              (map #(->> %
                         vals
                         (reduce +)))
              (reduce +))))

(defn no-data?
  [bands]
  (->> bands
       (map no-band-data?)
       (some false?)
       not))

(def some-data? (complement no-data?))

(defn row
  [system y]
  (map #(layer-bands system %)
       (for [x (xs system)] [x y])))

(defn maps-bands
  [system [x-start y-start]]
  (->> (for [y (ys system y-start)
             x (xs system x-start)]
         [x y])
       (map #(layer-bands system %))
       (drop-while #'no-band-data?)
       first))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Lifecycle Implementation   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord Layers [])

(defn start
  [this]
  (log/info "Starting map layers component ...")
  (log/debug "Started map layers component.")
  (assoc this :maps {:altitude (map-io/read-planet (config/altitude-map this))
                     :biomes (map-io/read-planet (config/altitude-map this))
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
