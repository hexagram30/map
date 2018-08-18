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
                           :center {:lon 22.5 :lat 89.88366013071895}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 200 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon 45.0 :lat 89.82549019607843}
                                     {:lon 0.0 :lat 89.82549019607843}
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
                           :center {:lon 112.5 :lat 89.88366013071895}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 600 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon 135.0 :lat 89.82549019607843}
                                     {:lon 90.0 :lat 89.82549019607843}
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
                           :center {:lon -112.5 :lat 89.88366013071895}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 1133 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon -90.0 :lat 89.82549019607843}
                                     {:lon -135.0 :lat 89.82549019607843}
                                     {:lon 0 :lat 90}]})
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
                           :center {:lon 22.5 :lat -89.88366013071895}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 200 :y 909}
                           :polygon [{:lon 0.0, :lat -89.82549019607843}
                                     {:lon 45.0, :lat -89.82549019607843}
                                     {:lon 0, :lat -90}
                                     {:lon 0.0, :lat -89.82549019607843}]})
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
                           :center {:lon 112.5 :lat -89.88366013071895}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 600 :y 909}
                           :polygon [{:lon 90.0, :lat -89.82549019607843}
                                     {:lon 135.0, :lat -89.82549019607843}
                                     {:lon 0, :lat -90}
                                     {:lon 90.0, :lat -89.82549019607843}]})
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
                           :center {:lon -112.5 :lat -89.88366013071895}
                           :depth nil
                           :land? true
                           :sea? false
                           :ice? true
                           :pixel {:x 1133 :y 909}
                           :polygon [{:lon -135.0, :lat -89.82549019607843}
                                     {:lon -90.0, :lat -89.82549019607843}
                                     {:lon 0, :lat -90}
                                     {:lon -135.0, :lat -89.82549019607843}]})
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
                           :center {:lon 22.5 :lat 89.88366013071895}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 200 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon 45.0 :lat 89.82549019607843}
                                     {:lon 0.0 :lat 89.82549019607843}
                                     {:lon 0 :lat 90}]})
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
                           :center {:lon 112.5 :lat 89.88366013071895}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 600 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon 135.0 :lat 89.82549019607843}
                                     {:lon 90.0 :lat 89.82549019607843}
                                     {:lon 0 :lat 90}]})
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
                           :center {:lon -112.5 :lat 89.88366013071895}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 1133 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon -90.0 :lat 89.82549019607843}
                                     {:lon -135.0 :lat 89.82549019607843}
                                     {:lon 0 :lat 90}]})
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
                           :center {:lon -112.5 :lat 89.88366013071895}
                           :depth -2000
                           :land? false
                           :sea? true
                           :ice? true
                           :pixel {:x 1133 :y 111}
                           :polygon [{:lon 0 :lat 90}
                                     {:lon -90.0 :lat 89.82549019607843}
                                     {:lon -135.0 :lat 89.82549019607843}
                                     {:lon 0 :lat 90}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix})))))
