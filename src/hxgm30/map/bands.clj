(ns hxgm30.map.bands
  (:require
    [hxgm30.map.components.config :as config]
    [hxgm30.map.components.layers :as layers]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log])
  (:import
    (clojure.lang Keyword)))

(defrecord Bands
  [altitude
   biome
   coords
   ls
   lsi])

(defn coords->bands
  [system [x y :as coords]]
  (->> (layers/maps system)
       (map (fn [[k v]] [(if (= k :biomes) :biome k)
                         (map-io/bands v x y)]))
       (into {:coords coords})
       (map->Bands)))

(defn all-bands
  ([system]
    (all-bands system [0 0]))
  ([system [x-start y-start]]
    (->> (for [y (util/ys system y-start)
               x (util/xs system x-start)]
           [x y])
         (map #(coords->bands system %))
         (drop-while #'util/no-band-data?))))

(defn first-band
  ([system]
    (first-band system [0 0]))
  ([system coords]
    (first (all-bands system coords))))

(defn unique-colors
  ([system ^Keyword field]
    (unique-colors system field [0 0]))
  ([system ^Keyword field coords]
    (->> coords
         (all-bands system)
         (map field)
         set
         (sort-by (juxt :red :green :blue)))))

(defn unique-altitudes
  ([system]
    (unique-altitudes system [0 0]))
  ([system coords]
    (unique-colors system :altitude coords)))

(defn unique-biomes
  ([system]
    (unique-biomes system [0 0]))
  ([system coords]
    (unique-colors system :biome coords)))
