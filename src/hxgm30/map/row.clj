(ns hxgm30.map.row
  (:require
    [hxgm30.map.bands :as bands]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log]))

(defrecord Row
  [data
   index])

(defn create
  [system y]
  (map->Row {:index y
             :data (->> (for [x (util/xs system)] [x y])
                        (map #(bands/coords->bands system %))
                        (remove #'util/no-band-data?))}))

(defn first?
  [system row-data]
  (and (util/some-data? (:data row-data))
       (util/no-data? (:data (create system (dec (:index row-data)))))))

(defn last?
  [system row-data]
  (and (util/some-data? (:data row-data))
       (util/no-data? (:data (create system (inc (:index row-data)))))))
