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
