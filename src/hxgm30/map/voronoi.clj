(ns hxgm30.map.voronoi
  (:require
    [delaunay-triangulation.core :as delaunay]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.util :as util]
    [voronoi-diagram.core :as voronoi])
  (:import
    (java.awt.image BufferedImage))
  (:refer-clojure :exclude [partition]))

;; XXX The initially selected delaunay and voronoi libraries seem to be buggy;
;;     odd exceptions get thrown as number of points passed hit the 1000 to
;;     2000 count. There's a Java library that may be more robust that we could
;;     try out:
;;     * https://github.com/aschlosser/voronoi-java

(def complete-cell? #(<= 3 (count %)))

(defn partition
  [points]
  (let [{:keys [cells edges points]} (voronoi/diagram points)]
    {:edges (util/round-nested edges)
     :cells (util/round-nested cells)
     :points (util/round-nested points)}))

(defn image-partition
  [^BufferedImage image points]
  (let [v-data (partition points)]
    v-data))

(defn dual
  [points]
  (let [{:keys [edges points triangles]} (delaunay/triangulate points)]
    {:edges (util/round-nested edges)
     :points (util/round-nested points)
     :triangles (util/round-nested triangles)}))

(defn image-dual
  [^BufferedImage image points]
  (let [d-data (dual points)]
    d-data))

(comment
  (def i (map-io/read-planet "001-mercator-bump-black-sea-crop-small"))
  (def ps (util/rand-points i 500))
  (def vs (voronoi/image-partition i ps))

  (def new-bmp (map-io/new-bmp (map-io/width i)
                               (map-io/height i)))
  (doseq [[p v] (partition 2 (interleave (:points vs) (:cells vs)))]
    (when (voronoi/complete-cell? v)
      (try (map-io/fill-polygon! new-bmp v (map-io/bands i p))
        (catch Exception _ex nil))))
  (map-io/write new-bmp "test.png"))
