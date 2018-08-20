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

(deftest bands->tile-above-equator
  (let [sys (system)
        current-row 514
        row-item-index 1
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon 0.3375, :lat 0.21929824561404132}
                           :depth -11000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 1 :y 514}
                           :polygon [{:lon 0.225 :lat 0.33082706766917624}
                                     {:lon 0.45 :lat 0.33082706766917624}
                                     {:lon 0.45 :lat 0.1077694235589064}
                                     {:lon 0.225 :lat 0.1077694235589064}
                                     {:lon 0.225 :lat 0.33082706766917624}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 514
        row-item-index 2
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon 0.5625 :lat 0.21929824561404132}
                           :depth -10000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 2 :y 514}
                           :polygon [{:lon 0.45 :lat 0.33082706766917624}
                                     {:lon 0.675 :lat 0.33082706766917624}
                                     {:lon 0.675 :lat 0.1077694235589064}
                                     {:lon 0.45 :lat 0.1077694235589064}
                                     {:lon 0.45 :lat 0.33082706766917624}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 514
        row-item-index 800
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon 0.11250000000001137 :lat 0.21929824561404132}
                           :depth -9000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 800 :y 514}
                           :polygon [{:lon 180.0 :lat 0.33082706766917624}
                                     {:lon -179.775 :lat 0.33082706766917624}
                                     {:lon -179.775 :lat 0.1077694235589064}
                                     {:lon 180.0 :lat 0.1077694235589064}
                                     {:lon 180.0 :lat 0.33082706766917624}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 514
        row-item-index 1599
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon -0.11249999999998295 :lat 0.21929824561404132}
                           :depth -12000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 1599 :y 514}
                           :polygon [{:lon -0.2249999999999659 :lat 0.33082706766917624}
                                     {:lon 0.0 :lat 0.33082706766917624}
                                     {:lon 0.0 :lat 0.1077694235589064}
                                     {:lon -0.2249999999999659 :lat 0.1077694235589064}
                                     {:lon -0.2249999999999659 :lat 0.33082706766917624}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix})))))

(deftest bands->tile-equator
  (let [sys (system)
        current-row 515
        row-item-index 1
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon 0.3375 :lat -0.003759398496235633}
                           :depth -10000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 1 :y 515}
                           :polygon [{:lon 0.225 :lat 0.1077694235589064}
                                     {:lon 0.45 :lat 0.1077694235589064}
                                     {:lon 0.45 :lat -0.11528822055137766}
                                     {:lon 0.225 :lat -0.11528822055137766}
                                     {:lon 0.225 :lat 0.1077694235589064}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 515
        row-item-index 2
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon 0.5625 :lat -0.003759398496235633}
                           :depth -10000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 2 :y 515}
                           :polygon [{:lon 0.45 :lat 0.1077694235589064}
                                     {:lon 0.675 :lat 0.1077694235589064}
                                     {:lon 0.675 :lat -0.11528822055137766}
                                     {:lon 0.45 :lat -0.11528822055137766}
                                     {:lon 0.45 :lat 0.1077694235589064}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 515
        row-item-index 800
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon 0.11250000000001137 :lat -0.003759398496235633}
                           :depth -9000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 800 :y 515}
                           :polygon [{:lon 180.0 :lat 0.1077694235589064}
                                     {:lon -179.775 :lat 0.1077694235589064}
                                     {:lon -179.775 :lat -0.11528822055137766}
                                     {:lon 180.0 :lat -0.11528822055137766}
                                     {:lon 180.0 :lat 0.1077694235589064}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 515
        row-item-index 1599
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon -0.11249999999998295 :lat -0.003759398496235633}
                           :depth -10000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 1599 :y 515}
                           :polygon [{:lon -0.2249999999999659 :lat 0.1077694235589064}
                                     {:lon 0.0 :lat 0.1077694235589064}
                                     {:lon 0.0 :lat -0.11528822055137766}
                                     {:lon -0.2249999999999659 :lat -0.11528822055137766}
                                     {:lon -0.2249999999999659 :lat 0.1077694235589064}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix})))))

(deftest bands->tile-below-equator
  (let [sys (system)
        current-row 516
        row-item-index 1
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon 0.3375 :lat -0.2268170426065126}
                           :depth -9000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 1 :y 516}
                           :polygon [{:lon 0.225 :lat -0.11528822055137766}
                                     {:lon 0.45 :lat -0.11528822055137766}
                                     {:lon 0.45 :lat -0.3383458646616475}
                                     {:lon 0.225 :lat -0.3383458646616475}
                                     {:lon 0.225       :lat -0.11528822055137766}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 516
        row-item-index 2
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon 0.5625 :lat -0.2268170426065126}
                           :depth -10000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 2 :y 516}
                           :polygon [{:lon 0.45 :lat -0.11528822055137766}
                                     {:lon 0.675 :lat -0.11528822055137766}
                                     {:lon 0.675 :lat -0.3383458646616475}
                                     {:lon 0.45 :lat -0.3383458646616475}
                                     {:lon 0.45 :lat -0.11528822055137766}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 516
        row-item-index 800
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon 0.11250000000001137 :lat -0.2268170426065126}
                           :depth -9000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 800 :y 516}
                           :polygon [{:lon 180.0 :lat -0.11528822055137766}
                                     {:lon -179.775 :lat -0.11528822055137766}
                                     {:lon -179.775 :lat -0.3383458646616475}
                                     {:lon 180.0 :lat -0.3383458646616475}
                                     {:lon 180.0 :lat -0.11528822055137766}]})
           (tile/bands->tile sys pixel-bands {:row-index row-index
                                              :row-item-index row-item-index
                                              :first? (row/first? sys row)
                                              :last? (row/last? sys row)
                                              :lon-per-pix lon-per-pix
                                              :lat-per-pix lat-per-pix}))))
  (let [sys (system)
        current-row 516
        row-item-index 1599
        row (row/create (system) current-row)
        row-index (- current-row (config/starting-row sys))
        pixel-bands (nth (:data row) row-item-index)
        lat-per-pix (util/lat-degrees-per-pixel sys)
        lon-per-pix (util/lon-degrees-per-pixel row)]
    (is (not (row/first? sys row)))
    (is (not (row/last? sys row)))
    (is (= 1600 (count (:data row))))
    (is (= (tile/map->Tile{:altitude nil
                           :biome :open-ocean
                           :center {:lon -0.11249999999998295 :lat -0.2268170426065126}
                           :depth -10000
                           :land? false
                           :sea? true
                           :ice? false
                           :pixel {:x 1599 :y 516}
                           :polygon [{:lon -0.2249999999999659 :lat -0.11528822055137766}
                                     {:lon 0.0 :lat -0.11528822055137766}
                                     {:lon 0.0 :lat -0.3383458646616475}
                                     {:lon -0.2249999999999659 :lat -0.3383458646616475}
                                     {:lon -0.2249999999999659 :lat -0.11528822055137766}]})
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
