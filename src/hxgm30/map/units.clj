(ns hxgm30.map.units)

(def league->strides 7811.736)
(def league->kms 5.556)
(def league->miles 3.452)

(def strides->m (/ (* league->kms 1000) league->strides))
(def m->strides (/ league->strides (* league->kms 1000)))
