(ns ^:system hxgm30.map.tests.system.components.config
  "Note: this namespace is exclusively for system tests; all tests defined
  here will use one or more system test fixtures.

  Definition used for system tests:
  * https://en.wikipedia.org/wiki/Software_testing#System_testing"
  (:require
    [clojure.test :refer :all]
    [hxgm30.map.components.config :as config]
    [hxgm30.map.testing.system :as test-system :refer [system]]))

(use-fixtures :once test-system/with-system)

(deftest land-colors
  (is (config/land? (system) {:blue 30 :green 120 :red 196}))
  (is (config/land? (system) {:blue 34 :green 136 :red 221}))
  (is (config/land? (system) {:blue 22 :green 90 :red 147}))
  (is (not (config/sea? (system) {:blue 22 :green 90 :red 147}))))

(deftest sea-colors
  (is (not (config/land? (system) {:blue 119 :green 51 :red 0})))
  (is (config/sea? (system) {:blue 119 :green 51 :red 0}))
  (is (config/sea? (system) {:blue 115 :green 49 :red 0})))

(deftest ice-colors
  (is (config/ice? (system) {:blue 255 :green 255 :red 255}))
  (is (config/ice? (system) {:blue 223 :green 223 :red 223}))
  (is (not (config/ice? (system) {:blue 119 :green 51 :red 0})))
  (is (not (config/ice? (system) {:blue 22 :green 90 :red 147}))))

(deftest altitude-lookup
  (is (= 12000
         (config/altitude-lookup (system) {:red 221 :green 136 :blue 34})))
  (is (= 12000
         (config/altitude-lookup (system) {:green 136 :red 221 :blue 34})))
  (is (= 12000
         (config/altitude-lookup (system) {:blue 34 :green 136 :red 221}))))

(deftest biome-lookup
  (is (= :coastal-lowlands
         (config/biome-lookup (system) {:red 235 :green 240 :blue 150})))
  (is (= :forest-conifer
         (config/biome-lookup (system) {:red 120 :green 112 :blue 7})))
  (is (= :forest-conifer
         (config/biome-lookup (system) {:red 156 :green 135 :blue 18})))
  (is (= :glacier
         (config/biome-lookup (system) {:red 255 :green 255 :blue 255})))
  (is (= :glacier
         (config/biome-lookup (system) {:red 200 :green 200 :blue 200})))
  (is (= :glacier
         (config/biome-lookup (system) {:red 181 :green 181 :blue 181}))))
