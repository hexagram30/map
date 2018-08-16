(ns hxgm30.map.tile
  (:require
    [hxgm30.map.components.config :as config]
    [hxgm30.map.io :as map-io]
    [taoensso.timbre :as log]))

(defrecord Tile
  [altitude
   biome
   center
   land?
   sea?
   ice?
   polygon])

(defn bands->tile
  [system pixel-bands]
  (map->Tile
    {:altitude (config/altitude-lookup system (:altitude pixel-bands))
     :biome (config/biome-lookup system (:biome pixel-bands))
     :center nil
     :ice? (config/ice? system (:lsi pixel-bands))
     :land? (config/land? system (:ls pixel-bands))
     :sea? (config/sea? system (:ls pixel-bands))
     :polygon nil}))

(defn row->tiles
  [system row]
  (map #(bands->tile system %) (:data row)))
