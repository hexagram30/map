(ns hxgm30.map.voronoi
  (:require
    [clojure.walk :refer [postwalk]]
    [delaunay-triangulation.core :as delaunay]
    [hxgm30.map.io :as map-io]
    [voronoi-diagram.core :as voronoi])
  (:import
    (java.awt.image BufferedImage))
  (:refer-clojure :exclude [partition]))

(defn rand-point
  [max-x max-y]
  [(rand-int (inc max-x))
   (rand-int (inc max-y))])

(defn rand-points
  ([^BufferedImage image point-count]
    (rand-points (map-io/width image)
                 (map-io/height image)
                 point-count))
  ([width height point-count]
    (map (fn [_] (rand-point width height)) (range (inc point-count)))))

(defn round-nested
  [data]
  (if (or (float? data) (double? data))
    (Math/round data)
    data))

(def complete-cell? #(<= 3 (count %)))

(defn partition
  [points]
  (let [{:keys [cells edges points]} (voronoi/diagram points)]
    {:edges (postwalk round-nested edges)
     :cells (postwalk round-nested cells)
     :points (postwalk round-nested points)}))

(defn image-partition
  [^BufferedImage image points]
  (let [v-data (partition points)]
    v-data))

(defn dual
  [points]
  (let [{:keys [edges points triangles]} (delaunay/triangulate points)]
    {:edges (postwalk round-nested edges)
     :points (postwalk round-nested points)
     :triangles (postwalk round-nested triangles)}))

(defn image-dual
  [^BufferedImage image points]
  (let [d-data (dual points)]
    d-data))

(comment
  (def i (map-io/read-planet "001-mercator-bump-black-sea-crop-small"))
  (def ps (voronoi/rand-points i 500))
  ;(voronoi/image-dual i ps)
  (def vs (voronoi/image-partition i ps))
  ; (def d (map-io/data i))

  (def new-bmp (map-io/new-bmp (map-io/width i)
                               (map-io/height i)))
  (doseq [[p v] (partition 2 (interleave (:points vs) (:cells vs)))]
    (when (voronoi/complete-cell? v)
      (try (map-io/fill-polygon! new-bmp v (map-io/bands i p))
        (catch Exception _ex nil))))
  (map-io/write new-bmp "test.png")
  )
