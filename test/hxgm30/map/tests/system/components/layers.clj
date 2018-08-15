(ns ^:system hxgm30.map.tests.system.components.layers
  "Note: this namespace is exclusively for system tests; all tests defined
  here will use one or more system test fixtures.

  Definition used for system tests:
  * https://en.wikipedia.org/wiki/Software_testing#System_testing"
  (:require
   [clojure.test :refer :all]
   [hxgm30.map.components.layers :as layers]
   [hxgm30.map.testing.system :as test-system :refer [system]]))

(use-fixtures :once test-system/with-system)

(deftest lat-degrees-per-pixel
  (is (= 0.17450980392156862
         (layers/lat-degrees-per-pixel (system)))))

(deftest first-row?
  (let [r0 (layers/row (system) 109)
        r1 (layers/row (system) 110)
        r2 (layers/row (system) 111)
        r3 (layers/row (system) 112)]
    (is (not (layers/first-row? (system) r0)))
    (is (not (layers/first-row? (system) r1)))
    (is (layers/first-row? (system) r2))
    (is (not (layers/first-row? (system) r3)))))

(deftest last-row?
  (let [r4 (layers/row (system) 908)
        r5 (layers/row (system) 909)
        r6 (layers/row (system) 910)
        r7 (layers/row (system) 911)]
    (is (not (layers/last-row? (system) r4)))
    (is (layers/last-row? (system) r5))
    (is (not (layers/last-row? (system) r6)))
    (is (not (layers/last-row? (system) r7)))))
