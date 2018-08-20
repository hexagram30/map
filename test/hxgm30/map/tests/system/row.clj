(deftest first-row-with-data?
  (let [r0 (row/create (system) 109)
        r1 (row/create (system) 110)
        r2 (row/create (system) 111)
        r3 (row/create (system) 112)]
    (is (not (row/first-row-with-data? (system) r0)))
    (is (not (row/first-row-with-data? (system) r1)))
    (is (row/first-row-with-data? (system) r2))
    (is (not (row/first-row-with-data? (system) r3)))))

(deftest last-row-with-data?
  (let [r4 (row/create (system) 908)
        r5 (row/create (system) 909)
        r6 (row/create (system) 910)
        r7 (row/create (system) 911)]
    (is (not (row/last-row-with-data? (system) r4)))
    (is (row/last-row-with-data? (system) r5))
    (is (not (row/last-row-with-data? (system) r6)))
    (is (not (row/last-row-with-data? (system) r7)))))

(deftest first-row?
  (let [r0 (row/create (system) 109)
        r1 (row/create (system) 110)
        r2 (row/create (system) 111)
        r3 (row/create (system) 112)]
    (is (not (row/first? (system) r0)))
    (is (not (row/first? (system) r1)))
    (is (row/first? (system) r2))
    (is (not (row/first? (system) r3)))))

(deftest last-row?
  (let [r4 (row/create (system) 908)
        r5 (row/create (system) 909)
        r6 (row/create (system) 910)
        r7 (row/create (system) 911)]
    (is (not (row/last? (system) r4)))
    (is (row/last? (system) r5))
    (is (not (row/last? (system) r6)))
    (is (not (row/last? (system) r7)))))
