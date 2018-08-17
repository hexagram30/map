(ns ^:unit hxgm30.map.tests.unit.util
  "Note: this namespace is exclusively for unit tests.

  Definition used for system tests:
  * https://en.wikipedia.org/wiki/Software_testing#System_testing"
  (:require
    [clojure.test :refer :all]
    [hxgm30.map.util :as util]))

(deftest color-map->hex
  (let [color-maps #{{:blue 0 :green 0 :red 0}
                     {:blue 0 :green 34 :red 68}
                     {:blue 0 :green 68 :red 0}
                     {:blue 0 :green 79 :red 22}
                     {:blue 0 :green 102 :red 34}
                     {:blue 0 :green 102 :red 51}
                     {:blue 0 :green 130 :red 102}
                     {:blue 0 :green 136 :red 34}
                     {:blue 0 :green 170 :red 119}
                     {:blue 0 :green 221 :red 187}
                     {:blue 11 :green 136 :red 147}
                     {:blue 17 :green 51 :red 85}
                     {:blue 22 :green 102 :red 153}
                     {:blue 22 :green 119 :red 164}
                     {:blue 34 :green 51 :red 119}
                     {:blue 34 :green 68 :red 136}
                     {:blue 34 :green 85 :red 153}
                     {:blue 34 :green 85 :red 170}
                     {:blue 34 :green 102 :red 187}
                     {:blue 34 :green 136 :red 204}
                     {:blue 34 :green 136 :red 221}
                     {:blue 34 :green 170 :red 238}
                     {:blue 34 :green 187 :red 255}
                     {:blue 115 :green 49 :red 0}
                     {:blue 116 :green 49 :red 0}
                     {:blue 116 :green 49 :red 1}
                     {:blue 116 :green 50 :red 1}
                     {:blue 117 :green 50 :red 1}
                     {:blue 117 :green 50 :red 2}
                     {:blue 118 :green 50 :red 2}
                     {:blue 119 :green 50 :red 3}
                     {:blue 119 :green 51 :red 0}
                     {:blue 120 :green 51 :red 3}
                     {:blue 120 :green 51 :red 4}
                     {:blue 121 :green 51 :red 4}
                     {:blue 121 :green 51 :red 5}
                     {:blue 122 :green 51 :red 5}
                     {:blue 122 :green 52 :red 6}
                     {:blue 123 :green 52 :red 6}
                     {:blue 124 :green 52 :red 7}
                     {:blue 125 :green 52 :red 7}
                     {:blue 125 :green 53 :red 8}
                     {:blue 126 :green 53 :red 8}
                     {:blue 126 :green 53 :red 9}
                     {:blue 127 :green 53 :red 9}
                     {:blue 128 :green 54 :red 10}
                     {:blue 133 :green 56 :red 11}
                     {:blue 157 :green 65 :red 23}
                     {:blue 170 :green 170 :red 170}
                     {:blue 179 :green 179 :red 179}
                     {:blue 185 :green 185 :red 185}
                     {:blue 188 :green 188 :red 188}
                     {:blue 196 :green 196 :red 196}
                     {:blue 205 :green 205 :red 205}
                     {:blue 214 :green 214 :red 214}
                     {:blue 255 :green 255 :red 255}}]
  (is (= ["0x000000"
          "0x003173"
          "0x003174"
          "0x003377"
          "0x004400"
          "0x013174"
          "0x013274"
          "0x013275"
          "0x023275"
          "0x023276"
          "0x033277"
          "0x033378"
          "0x043378"
          "0x043379"
          "0x053379"
          "0x05337a"
          "0x06347a"
          "0x06347b"
          "0x07347c"
          "0x07347d"
          "0x08357d"
          "0x08357e"
          "0x09357e"
          "0x09357f"
          "0x0a3680"
          "0x0b3885"
          "0x164f00"
          "0x17419d"
          "0x226600"
          "0x228800"
          "0x336600"
          "0x442200"
          "0x553311"
          "0x668200"
          "0x773322"
          "0x77aa00"
          "0x884422"
          "0x93880b"
          "0x995522"
          "0x996616"
          "0xa47716"
          "0xaa5522"
          "0xaaaaaa"
          "0xb3b3b3"
          "0xb9b9b9"
          "0xbb6622"
          "0xbbdd00"
          "0xbcbcbc"
          "0xc4c4c4"
          "0xcc8822"
          "0xcdcdcd"
          "0xd6d6d6"
          "0xdd8822"
          "0xeeaa22"
          "0xffbb22"
          "0xffffff"]
         (sort (map #'util/color-map->hex color-maps))))))

(deftest normalize-longitude
  (is (= 0 (util/normalize-longitude 0)))
  (is (= 45 (util/normalize-longitude 45)))
  (is (= 90 (util/normalize-longitude 90)))
  (is (= 135 (util/normalize-longitude 135)))
  (is (= 180 (util/normalize-longitude 180)))
  (is (= -135 (util/normalize-longitude 225)))
  (is (= -90 (util/normalize-longitude 270)))
  (is (= -45 (util/normalize-longitude 315)))
  (is (= 0 (util/normalize-longitude 360))))
