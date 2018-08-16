(ns hxgm30.map.bands
  (:require
    [hxgm30.map.components.config :as config]
    [hxgm30.map.components.layers :as layers]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log]))

(defrecord Bands
  [altitude
   biome
   coords
   ls
   lsi])

(defn coords->bands
  [system [x y :as coords]]
  (->> (layers/maps system)
       (map (fn [[k v]] [(if (= k :bioms) :biom k)
                         (map-io/bands v x y)]))
       (into {:coords coords})
       (map->Bands)))

(defn all-bands
  [system [x-start y-start]]
  (->> (for [y (util/ys system y-start)
             x (util/xs system x-start)]
         [x y])
       (map #(coords->bands system %))
       (drop-while #'util/no-band-data?)))

(defn first-band
  [system coords]
  (first (all-bands system coords)))
