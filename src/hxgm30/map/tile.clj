(ns hxgm30.map.tile
  (:require
    [hxgm30.map.components.config :as config]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.row :as row]
    [hxgm30.map.util :as util]
    [taoensso.timbre :as log]))

(defrecord Tile
  [altitude
   biome
   center
   depth
   land?
   sea?
   ice?
   pixel
   polygon])

(def ^:private triangle-north-point
  {:lon 0 :lat 90})

(def ^:private triangle-south-point
  {:lon 0 :lat -90})

(defn- triagnle-lr-point
  [row-item-index lon-per-pix lat-per-pix final-lat]
  {:lon (util/normalize-longitude (* (inc row-item-index) lon-per-pix))
   :lat (- final-lat lat-per-pix)})

(defn- triagnle-ll-point
  [row-item-index lon-per-pix lat-per-pix final-lat]
  {:lon (util/normalize-longitude (* row-item-index lon-per-pix))
   :lat (- final-lat lat-per-pix)})

(defn geodesic-triangle-north
  "Starting at the pole, all of the initial points for all of the triangles
  will have the same value, since they are at the same place. Moving clock-wise
  from there, the next points will all share the same latitude, but have
  longitudes that differ one from next by how many degress longitude there are
  per pixel and the row index of the given tile (geodesic triangle). Again,
  moving clockwise, the final points for each triangle will also be at the same
  degree latitude, but will have a longitude less by one tile width than the
  previous point.

  Note that `row-item-index` is the zero-based index for the array of in the
  x-direction (in other words, the index for a given element in the list of
  items for a row collection).

  Finally, we follow GIS convention and indicate a closed polygon by adding an
  additional point: the last point is the same as the first."
  [{:keys [row-item-index lon-per-pix lat-per-pix]}]
  (let [first-point triangle-north-point
        second-point (triagnle-lr-point
                      row-item-index lon-per-pix lat-per-pix 90)
        third-point (triagnle-ll-point
                     row-item-index lon-per-pix lat-per-pix 90)]
    [first-point second-point third-point first-point]))

(defn geodesic-triangle-south
  "This function embodies the the same logic as its north pole counterpart;
  see the docstring of `geodesic-triangle-north` for more details."
  [{:keys [row-item-index lon-per-pix lat-per-pix]}]
  (let [first-point (triagnle-ll-point row-item-index
                                         lon-per-pix
                                         (* -1 lat-per-pix)
                                         -90)
        second-point (triagnle-lr-point row-item-index
                                          lon-per-pix
                                          (* -1 lat-per-pix)
                                          -90)
        third-point triangle-south-point]
    [first-point second-point third-point first-point]))

(defn center-north-triangle
  "We just need a rough approximation here, to get a point somewhere away from
  the edges of the triangle."
  [{:keys [row-item-index lon-per-pix lat-per-pix]}]
  {:lat (- 90 (* (/ 2 3) lat-per-pix))
   :lon (util/normalize-longitude
         (- (* (inc row-item-index) lon-per-pix) (/ lon-per-pix 2)))})

(defn center-south-triangle
  "We just need a rough approximation here, to get a point somewhere away from
  the edges of the triangle."
  [{:keys [row-item-index lon-per-pix lat-per-pix]}]
  {:lat (+ -90 (* (/ 2 3) lat-per-pix))
   :lon (util/normalize-longitude
         (- (* (inc row-item-index) lon-per-pix) (/ lon-per-pix 2)))})

(defn geodesic-rectangle
  "TBD

  Note that `row-item-index` is the zero-based index for the array of in the
  x-direction (in other words, the index for a given element in the list of
  items for a row collection). Conversely, the row-index represents which row
  is currently being processed, thus giving us a value for the current,
  relative place in the y-direction."
  [{:keys [row-item-index row-index lon-per-pix lat-per-pix]}]
  :not-implemented)

(defn center-rectangle
  [{:keys [row-item-index row-index lon-per-pix lat-per-pix]}]
  :not-implemented)

(defn polygon
  [coords {:keys [first? last?] :as opts}]
  (cond first? (geodesic-triangle-north opts)
        last? (geodesic-triangle-south opts)
        :else (geodesic-rectangle opts)))

(defn center-polygon
  [coords {:keys [first? last?] :as opts}]
  (cond first? (center-north-triangle opts)
        last? (center-south-triangle opts)
        :else (center-rectangle opts)))

(defn bands->tile
  [system pixel-bands opts]
  (let [coords (:coords pixel-bands)
        sea? (config/sea? system (:ls pixel-bands))
        altitude (config/altitude-lookup system (:altitude pixel-bands))]
    (map->Tile
      {:altitude (if sea? nil altitude)
       :biome (config/biome-lookup system (:biome pixel-bands))
       :center (center-polygon coords opts)
       :depth (if sea? altitude nil)
       :ice? (config/ice? system (:lsi pixel-bands))
       :land? (config/land? system (:ls pixel-bands))
       :sea? sea?
       :pixel (zipmap [:x :y] coords)
       :polygon (polygon coords opts)})))

(defn row->tiles
  [system row row-index]
  (let [lat-per-pix (util/lat-degrees-per-pixel system)
        lon-per-pix (util/lon-degrees-per-pixel row)
        first? (row/first? system row)
        last? (row/last? system row)]
    (->> row
         :data
         (map-indexed vector)
         (map #(bands->tile system %2 {:row-item-index %1
                                       :row-index row-index
                                       :first? first?
                                       :last? last?
                                       :lat-per-pix lat-per-pix
                                       :lon-per-pix lon-per-pix})))))
