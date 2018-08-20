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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Utility Constants and Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def ^:private triangle-north-point
  {:lon 0 :lat 90})

(def ^:private triangle-south-point
  {:lon 0 :lat -90})

(defn- triangle-base-r-point
  ([{:keys [lat-per-pix] :as opts} final-lat]
    (triangle-base-r-point opts lat-per-pix final-lat))
  ([{:keys [row-item-index lon-per-pix]} lat-per-pix final-lat]
    {:lon (util/normalize-longitude (* (inc row-item-index) lon-per-pix))
     :lat (- final-lat lat-per-pix)}))

(defn- triangle-base-l-point
  ([{:keys [lat-per-pix] :as opts} final-lat]
    (triangle-base-l-point opts lat-per-pix final-lat))
  ([{:keys [row-item-index lon-per-pix]} lat-per-pix final-lat]
    {:lon (util/normalize-longitude (* row-item-index lon-per-pix))
     :lat (- final-lat lat-per-pix)}))

(defn rectangle-ul-point
  [{:keys [row-index row-item-index lon-per-pix lat-per-pix]}]
  {:lon (util/normalize-longitude (* row-item-index lon-per-pix))
   :lat (util/normalize-latitude (* (dec row-index) lat-per-pix))})

(defn rectangle-ur-point
  [{:keys [row-index row-item-index lon-per-pix lat-per-pix]}]
  {:lon (util/normalize-longitude (* (inc row-item-index) lon-per-pix))
   :lat (util/normalize-latitude (* (dec row-index) lat-per-pix))})

(defn rectangle-lr-point
  [{:keys [row-index row-item-index lon-per-pix lat-per-pix]}]
  {:lon (util/normalize-longitude (* (inc row-item-index) lon-per-pix))
   :lat (util/normalize-latitude (* row-index lat-per-pix))})

(defn rectangle-ll-point
  [{:keys [row-index row-item-index lon-per-pix lat-per-pix]}]
  {:lon (util/normalize-longitude (* row-item-index lon-per-pix))
   :lat (util/normalize-latitude (* row-index lat-per-pix))})

(defn biome-check
  [system pixel-bands sea? depth]
  (let [biome (config/biome-lookup system (:biome pixel-bands))]
    (if (and (nil? biome) sea?)
      (if (>= depth -1000)
        :coastal-waters
        :open-ocean)
      biome)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Support Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
  [opts]
  (let [second-point (triangle-base-r-point opts 90)
        third-point (triangle-base-l-point opts 90)]
    [triangle-north-point
     second-point
     third-point
     triangle-north-point]))

(defn geodesic-triangle-south
  "This function embodies the the same logic as its north pole counterpart;
  see the docstring of `geodesic-triangle-north` for more details."
  [{:keys [lat-per-pix] :as opts}]
  (let [first-point (triangle-base-l-point opts
                                           (* -1 lat-per-pix)
                                           -90)
        second-point (triangle-base-r-point opts
                                            (* -1 lat-per-pix)
                                            -90)]
    [first-point second-point triangle-south-point first-point]))

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
  "Provide a polygon of points representing a geodesic rectangle on a globe
  being computed from a pixel map, where each pixel is being transformed into
  a geodesic rectangle. The ordering of points in the rectangle starts at the
  upper-left and goes in a clock-wise manner.

  Note that `row-item-index` is the zero-based index for the array of in the
  x-direction (in other words, the index for a given element in the list of
  items for a row collection). Conversely, the row-index represents which row
  is currently being processed, thus giving us a value for the current,
  relative place in the y-direction."
  [opts]
  (let [first-point (rectangle-ul-point opts)]
    [first-point
     (rectangle-ur-point opts)
     (rectangle-lr-point opts)
     (rectangle-ll-point opts)
     first-point]))

(defn center-rectangle
  [opts]
  ;; Note that the lat/lon here don't need to be normalized, since they are
  ;; pulled from values that have already been normalized.
  (let [ll (rectangle-ll-point opts)
        ur (rectangle-ur-point opts)]
    {:lon (+ (/ (- (:lon ur) (:lon ll)) 2) (:lon ll))
     :lat (+ (/ (- (:lat ur) (:lat ll)) 2) (:lat ll))}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   API   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
        altitude (config/altitude-lookup system (:altitude pixel-bands))
        depth (if sea? altitude nil)]
    (map->Tile
      {:altitude (if sea? nil altitude)
       :biome (biome-check system pixel-bands sea? depth)
       :center (center-polygon coords opts)
       :depth depth
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
