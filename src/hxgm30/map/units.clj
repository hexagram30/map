(ns hxgm30.map.units
  "Note that some of the work done here was figured out in the following
  ticket:

  * https://github.com/hexagram30/map/issues/1")

(def league->strides 7811.736) ; 1 league has this many strides
(def league->kms 5.556) ; 1 league has this many kilometers
(def league->miles 3.452) ; 1 league has this many miles

(def strides->m (/ (* league->kms 1000) league->strides)) ; strides in meter
(def m->strides (/ league->strides (* league->kms 1000))) ; meters in stride

(def walking-speed 1) ; league/hour
(def strides-per-hour league->strides)

(def average-city-radius 0.28) ; leagues
(def average-city-circumference (* 2 Math/PI average-city-radius)) ; leagues
(def average-city-area 0.25) ; square leagues

(def highest-mountain 16000) ; strides
(def planet-circumference 7200) ; leagues
