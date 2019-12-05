(ns ^:unit hxgm30.map.tests.unit.biome
  "Note: this namespace is exclusively for unit tests.

  Definition used for system tests:
  * https://en.wikipedia.org/wiki/Software_testing#System_testing"
  (:require
    [clojure.test :refer :all]
    [hxgm30.map.biome.core :as biome]
    [hxgm30.map.biome.temperature :as biome-tmp]))

(deftest cols-rows-biomes-count
  (is (= 11 biome/rows))
  (is (= biome/rows (count biome/biomes-matrix)))
  (is (= 9 biome/cols))
  (is (= biome/cols (count (first biome/biomes-matrix))))
  (is (= 42 (count biome/biomes))))

(deftest sorted
  (is (= #{273.15
           274.65
           276.15
           279.15
           285.15
           288.9840949292747
           297.15
           309.5271975962495
           321.15
           345.90439519249907
           369.15}
         biome/sorted-temps))
  (is (= #{62.5
          125.0
          250.0
          500.0
          1000.0
          2000.0
          4000.0
          8000.0
          16000.0}
         biome/sorted-precips)))

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

(deftest biomes
  (is (= 42 (count biome/biomes)))
  (is (= {:color "#ffffff" :name "Polar / Alvar Desert"}
         (nth biome/biomes 0)))
  (is (= {:color "#80ad85"
          :name "Warm Temperate / Lower Montane Thorn Steppe / Woodland"}
         (nth biome/biomes 20)))
  (is (= {:color "#3ebc8a" :name "Tropical Rain Forest"}
         (nth biome/biomes 41))))

(deftest nearest-biome
  (is (= {:color "#ffffff", :name "Polar / Alvar Desert"}
       (biome/nearest -100 0)))
  (is (= {:color "#ffffff", :name "Polar / Alvar Desert"}
         (biome/nearest 100 270)))
  (is (= {:color "#368892", :name "Boreal / Subalpine Rain Forest"}
         (biome/nearest 1000 280)))
  (is (= {:color "#3ebc8a", :name "Tropical Rain Forest"}
         (biome/nearest 16000 320)))
  (is (= {:color "#57c784", :name "Tropical Moist Forest"}
         (biome/nearest 20000 400)))
  (is (= {:color "#d4d28c", :name "Tropical Desert"}
         (biome/nearest 0 400)))
  (is (= {:color "#9fb385", :name "Warm Temperate / Lower Montane Desert Scrub"}
         (biome/nearest 100 290))))
