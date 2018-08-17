(ns ^:system hxgm30.map.tests.system.core
  "Note: this namespace is exclusively for system tests; all tests defined
  here will use one or more system test fixtures.

  Definition used for system tests:
  * https://en.wikipedia.org/wiki/Software_testing#System_testing"
  (:require
    [clojure.test :refer :all]
    [hxgm30.map.bands :as bands]
    [hxgm30.map.components.config :as config]
    [hxgm30.map.row :as row]
    [hxgm30.map.tile :as tile]
    [hxgm30.map.util :as util]
    [hxgm30.map.testing.system :as test-system :refer [system]]))

(use-fixtures :once test-system/with-system)

(load "bands")
(load "row")
(load "tile")
(load "util")
