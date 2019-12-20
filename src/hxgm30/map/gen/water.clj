(ns hxgm30.map.gen.water
  (:require
    [hxgm30.map.io :as map-io]
    [hxgm30.map.util :as util]))

(defn neighbors
  [[center-x center-y]]
  (remove #(= [center-x center-y] %)
          (for [x (range (dec center-x) (+ 2 center-x))
                y (range (dec center-y) (+ 2 center-y))]
            [x y])))

(defn color-distance
  [c1 c2]
  (->> (vals c1)
       (interleave (vals c2))
       (partition 2)
       (map #(apply - %))
       (map #(Math/pow % 2))
       (reduce +)
       (Math/sqrt)))

(defn sign
  [number]
  (cond (zero? number) 0
        (pos? number) 1
        :else -1))

(defn color-direction
  [c1 c2]
  (sign (- (reduce + (vals c1))
           (reduce + (vals c2)))))

(defn colors-xducer
  [image]
  (map #(map-io/bands image %)))

(defn color-distances-xducer
  [c1]
  (map #(color-distance c1 %)))

(defn color-directions-xducer
  [c1]
  (map #(color-direction c1 %)))

(defn neighbors-colors
  [image point]
  (transduce (colors-xducer image) conj (neighbors point)))

(defn neighbors-color-distances
  [image point]
  (transduce (color-distances-xducer (map-io/bands image point))
             conj
             (neighbors-colors image point)))

(defn neighbors-color-directions
  [image point]
  (transduce (color-directions-xducer (map-io/bands image point))
             conj
             (neighbors-colors image point)))

(defn neighbors-data
  [image point]
  (let [nbrs (neighbors point)
        point-color (map-io/bands image point)
        colors (transduce (colors-xducer image) conj nbrs)
        directions (transduce (color-directions-xducer point-color) conj colors)
        dsts (transduce (color-distances-xducer point-color) conj colors)
        data [nbrs colors directions dsts]]
    (->> data
         (apply interleave)
         (partition (count data))
         (map #(zipmap [:point :color :direction :distance] %)))))

(defn lowest-neighbor
  [image point]
  (let [data (neighbors-data image point)]
    (->> data
         (map #(update-in % [:distance] * (:direction %)))
         (sort-by :distance)
         first
         :point)))

(comment
  (def i (map-io/read-planet "ilunao/bump-black-sea-crop-small"))
  (def new-bmp (map-io/new-bmp (map-io/width i)
                               (map-io/height i)))
  (def start [150 170])
  (def water-color (map-io/components->color {:red 0 :green 0 :blue 255}))

  (water/neighbors-data i start)
  (water/lowest-neighbor i start)
  )
