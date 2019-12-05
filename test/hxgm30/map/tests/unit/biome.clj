(ns ^:unit hxgm30.map.tests.unit.biome
  "Note: this namespace is exclusively for unit tests.

  Definition used for system tests:
  * https://en.wikipedia.org/wiki/Software_testing#System_testing"
  (:require
    [clojure.test :refer :all]
    [hxgm30.map.biome.core :as biome]
    [hxgm30.map.biome.temperature :as biome-tmp]))

(deftest nearest-temp
  (is (= 273.15 (biome/nearest-temp (+ -100 biome-tmp/K))))
  (is (= 273.15 (biome/nearest-temp (+ -10 biome-tmp/K))))
  (is (= 273.15 (biome/nearest-temp (+ 0 biome-tmp/K))))
  (is (= 285.15 (biome/nearest-temp (+ 12.337 biome-tmp/K))))
  (is (= 309.5271975962495 (biome/nearest-temp (+ 33.4 biome-tmp/K))))
  (is (= 369.15 (biome/nearest-temp (+ 129.45 biome-tmp/K))))
  (is (= 369.15 (biome/nearest-temp (+ 1000 biome-tmp/K)))))

(deftest nearest-precip
  (is (= 62.5 (biome/nearest-precip 0)))
  (is (= 62.5 (biome/nearest-precip 10)))
  (is (= 125.0 (biome/nearest-precip 100)))
  (is (= 1000.0 (biome/nearest-precip 1000)))
  (is (= 8000.0 (biome/nearest-precip 10000)))
  (is (= 16000.0 (biome/nearest-precip 100000))))
