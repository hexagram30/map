(deftest bands->tile-north-pole
  (let [sys (system)
        current-row 111
        row-item-index 0
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (row/first? sys row))
    (is (= 8 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :glacier
                           :center {:lon 22.5 :lat 89.85129490392649}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 200 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon 45.0 :lat 89.77694235588973}
                                     {:lon 0.0 :lat 89.77694235588973}
                                     {:lon 0 :lat 90}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 111
        row-item-index 2
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (row/first? sys row))
    (is (= 8 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :glacier
                           :center {:lon 112.5 :lat 89.85129490392649}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 600 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon 135.0 :lat 89.77694235588973}
                                     {:lon 90.0 :lat 89.77694235588973}
                                     {:lon 0 :lat 90}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 111
        row-item-index 5
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (row/first? sys row))
    (is (= 8 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :glacier
                           :center {:lon -112.5 :lat 89.85129490392649}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 1133 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon -90.0 :lat 89.77694235588973}
                                     {:lon -135.0 :lat 89.77694235588973}
                                     {:lon 0 :lat 90}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix})))))

(deftest bands->tile-high-lat
  (let [sys (system)
        current-row 112
        row-item-index 1
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 16 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :glacier
                           :center {:lon 33.75 :lat 89.88847117794487}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 67 :y 112}
                           :polygon [{:lon 22.5 :lat 90.0}
                                     {:lon 45.0 :lat 90.0}
                                     {:lon 45.0 :lat 89.77694235588973}
                                     {:lon 22.5 :lat 89.77694235588973}
                                     {:lon 22.5 :lat 90.0}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 112
        row-item-index 2
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 16 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :glacier
                           :center {:lon 56.25 :lat 89.88847117794487}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 200 :y 112}
                           :polygon [{:lon 45.0 :lat 90.0}
                                     {:lon 67.5 :lat 90.0}
                                     {:lon 67.5 :lat 89.77694235588973}
                                     {:lon 45.0 :lat 89.77694235588973}
                                     {:lon 45.0 :lat 90.0}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 112
        row-item-index 8
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 16 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :glacier
                           :center {:lon 11.25 :lat 89.88847117794487}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 866 :y 112}
                           :polygon [{:lon 180.0 :lat 90.0}
                                     {:lon -157.5 :lat 90.0}
                                     {:lon -157.5 :lat 89.77694235588973}
                                     {:lon 180.0 :lat 89.77694235588973}
                                     {:lon 180.0 :lat 90.0}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 112
        row-item-index 15
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 16 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :glacier
                           :center {:lon -11.25 :lat 89.88847117794487}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 1533 :y 112}
                           :polygon [{:lon -22.5 :lat 90.0}
                                     {:lon 0.0 :lat 90.0}
                                     {:lon 0.0 :lat 89.77694235588973}
                                     {:lon -22.5 :lat 89.77694235588973}
                                     {:lon -22.5 :lat 90.0}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix})))))

(deftest bands->tile-low-lat
  (let [sys (system)
        current-row 908
        row-item-index 1
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 16 (count (:data row))))
    (is (= (tile/map->Tile{:altitude 11000
                           :biome :glacier
                           :center {:lon 33.75 :lat -87.66541353383457}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 67 :y 908}
                           :polygon [{:lon 22.5 :lat -87.55388471177943}
                                     {:lon 45.0 :lat -87.55388471177943}
                                     {:lon 45.0 :lat -87.7769423558897}
                                     {:lon 22.5 :lat -87.7769423558897}
                                     {:lon 22.5 :lat -87.55388471177943}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 908
        row-item-index 2
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 16 (count (:data row))))
    (is (= (tile/map->Tile{:altitude 11000
                           :biome :glacier
                           :center {:lon 56.25 :lat -87.66541353383457}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 200 :y 908}
                           :polygon [{:lon 45.0 :lat -87.55388471177943}
                                     {:lon 67.5 :lat -87.55388471177943}
                                     {:lon 67.5 :lat -87.7769423558897}
                                     {:lon 45.0 :lat -87.7769423558897}
                                     {:lon 45.0 :lat -87.55388471177943}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 908
        row-item-index 8
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 16 (count (:data row))))
    (is (= (tile/map->Tile{:altitude 11000
                           :biome :glacier
                           :center {:lon 11.25 :lat -87.66541353383457}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 866 :y 908}
                           :polygon [{:lon 180.0 :lat -87.55388471177943}
                                     {:lon -157.5 :lat -87.55388471177943}
                                     {:lon -157.5 :lat -87.7769423558897}
                                     {:lon 180.0 :lat -87.7769423558897}
                                     {:lon 180.0 :lat -87.55388471177943}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 908
        row-item-index 15
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 16 (count (:data row))))
    (is (= (tile/map->Tile{:altitude 10000
                           :biome :glacier
                           :center {:lon -11.25 :lat -87.66541353383457}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 1533 :y 908}
                           :polygon [{:lon -22.5 :lat -87.55388471177943}
                                     {:lon 0.0 :lat -87.55388471177943}
                                     {:lon 0.0 :lat -87.7769423558897}
                                     {:lon -22.5 :lat -87.7769423558897}
                                     {:lon -22.5 :lat -87.55388471177943}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix})))))

(deftest bands->tile-south-pole
  (let [sys (system)
        current-row 909
        row-item-index 0
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (row/last? sys row))
    (is (= 8 (count (:data row))))
    (is (= (tile/map->Tile{:altitude 14000
                           :biome :glacier
                           :center {:lon 22.5 :lat -89.85129490392649}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 200 :y 909}
                           :polygon [{:lon 0.0, :lat -89.77694235588973}
                                     {:lon 45.0 :lat -89.77694235588973}
                                     {:lon 0 :lat -90}
                                     {:lon 0.0 :lat -89.77694235588973}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 909
        row-item-index 2
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (row/last? sys row))
    (is (= 8 (count (:data row))))
    (is (= (tile/map->Tile{:altitude 11000
                           :biome :glacier
                           :center {:lon 112.5 :lat -89.85129490392649}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 600 :y 909}
                           :polygon [{:lon 90.0 :lat -89.77694235588973}
                                     {:lon 135.0 :lat -89.77694235588973}
                                     {:lon 0 :lat -90}
                                     {:lon 90.0 :lat -89.77694235588973}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 909
        row-item-index 5
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (row/last? sys row))
    (is (= 8 (count (:data row))))
    (is (= (tile/map->Tile{:altitude 11000
                           :biome :glacier
                           :center {:lon -112.5 :lat -89.85129490392649}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 1133 :y 909}
                           :polygon [{:lon -135.0 :lat -89.77694235588973}
                                     {:lon -90.0 :lat -89.77694235588973}
                                     {:lon 0 :lat -90}
                                     {:lon -135.0 :lat -89.77694235588973}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix})))))
