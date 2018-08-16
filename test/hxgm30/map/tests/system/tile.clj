(deftest bands->tile
  (let [bands (first (:data (row/create (system) 111)))]
    (is (= (tile/map->Tile{:altitude nil
                           :biome nil
                           :center nil
                           :land? false
                           :sea? true
                           :ice? true
                           :polygon nil})
           (tile/bands->tile (system) bands)))))
