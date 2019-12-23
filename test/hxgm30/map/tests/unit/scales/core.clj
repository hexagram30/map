(ns ^:unit hxgm30.map.tests.unit.scales.core
  "Note: this namespace is exclusively for unit tests.

  Definition used for system tests:
  * https://en.wikipedia.org/wiki/Software_testing#System_testing"
  (:require
   [clojure.test :refer :all]
   [hxgm30.map.scales.core :as scales])
  (:import
   (hxgm30.map.scales.core ScaledRange
                           PrecipitationRange
                           TemperatureRange)
   (hxgm30.map.scales.precipitation LinearPrecipitationRange)
   (hxgm30.map.scales.temperature LinearTemperatureRange
                                  SineTemperatureRange)))

(deftest temperature-scale
  (let [ts (scales/new-scale :temperature :linear)]
    (is (record? ts))
    (is (= LinearTemperatureRange (type ts) ))
    ;; Check the protocol
    (is (satisfies? scales/ScaledRange ts))
    (is (satisfies? scales/TemperatureRange ts))
    (is (extends? scales/ScaledRange LinearTemperatureRange))
    (is (extends? scales/TemperatureRange LinearTemperatureRange))
    ;; Check some methods
    (is (= 222 (:normalized-min ts)))
    (is (= 222 (scales/get-normalized-min ts))))
  (let [ts (scales/new-scale :temperature :sine)]
    (is (record? ts))
    (is (= SineTemperatureRange (type ts) ))
    ;; Check the protocol
    (is (satisfies? scales/ScaledRange ts))
    (is (satisfies? scales/TemperatureRange ts))
    (is (extends? scales/ScaledRange SineTemperatureRange))
    (is (extends? scales/TemperatureRange SineTemperatureRange))
    ;; Check some methods
    (is (= -0.7853981633974483 (:normalized-min ts)))
    (is (= -0.7853981633974483 (scales/get-normalized-min ts)))))

(deftest precipitation-scale
  (let [ps (scales/new-scale :precipitation :linear)]
    (is (record? ps))
    (is (= LinearPrecipitationRange (type ps) ))
    ;; Check the protocol
    (is (satisfies? scales/ScaledRange ps))
    (is (satisfies? scales/PrecipitationRange ps))
    (is (extends? scales/ScaledRange LinearPrecipitationRange))
    (is (extends? scales/PrecipitationRange LinearPrecipitationRange))
    ;; Check some methods
    (is (= 0 (:normalized-min ps)))
    (is (= 0 (scales/get-normalized-min ps)))))
