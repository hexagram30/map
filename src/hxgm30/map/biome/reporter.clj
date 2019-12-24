(ns hxgm30.map.biome.reporter
  (:require
   [hxgm30.map.io :as map-io]
   [hxgm30.map.biome.core :as biome]
   [hxgm30.map.scales.core :as scales]
   [hxgm30.map.util :as util]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Shared Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-percent
  [fq total]
  (* 100 (float (/ fq total))))

(defn format-percent
  [p]
  (format "%.1f%%" p))

(defn get-frequency-data
  [total [rgb fq]]
  (let [color-map (util/rgb-pixel->color-map rgb)]
    {:count fq
     :percent (format-percent (get-percent fq total))
     :rgb rgb
     :color-map color-map
     :hex (util/clj-hex->html (util/rgb-pixel->hex rgb))
     :ansi (util/color-map->ansi color-map)}))

(defn get-stats
  [im]
  (let [rgbs-fqs (frequencies (sort (map-io/all-pixels im)))
        total (reduce + (vals rgbs-fqs))
        fdata (map (partial get-frequency-data total) rgbs-fqs)]
    {:freqs fdata
     :total total}))

(defn print-data
  ([stats header divider formatter]
   (print-data stats header divider formatter {:sort-by :count}))
  ([stats header divider formatter opts]
   (let [fqs (reverse (sort-by (:sort-by opts) (:freqs stats)))
         ]
     (println header)
     (println divider)
     (doall
      (for [fq fqs]
        (println (formatter fq))))
     (println (format "\nTotal counts: %d" (:total stats))))
   :ok))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Precipitation Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def precip-format-str "%7s%8s%8s%8s %13s")
(def precip-header (format precip-format-str
                           "Color" "Hex" "Count" "Percent" "Precipitation"))
(def precip-divider (format precip-format-str
                            "-------" "-------" "-------" "-------"
                            "-------------"))

(defn precip-freq-formatter
  [fq]
  (format precip-format-str
          (:ansi fq)
          (:hex fq)
          (:count fq)
          (:percent fq)
          (:precip-str fq)))

(defn format-precip
  [p]
  (format "%,d mm/yr" p))

(defn add-precip-datum
  [data]
  (let [precip (scales/precipitation-amount biome/ps (:color-map data))]
    (assoc data :precip-int precip
                :precip-str (format-precip precip))))

(defn add-precip-data
  [stats]
  (assoc stats :freqs (map add-precip-datum (:freqs stats))))

(defn get-precip-stats
  [im]
  (add-precip-data (get-stats im)))

(defn print-precips
  ([stats]
   (print-precips stats {:sort-by :count}))
  ([stats opts]
   (print-data stats precip-header precip-divider precip-freq-formatter opts)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Temperature Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def temp-format-str "%7s%8s%8s%8s   %-12s")
(def temp-header (format temp-format-str
                         "Color" "Hex" "Count" "Percent" " Temperature"))
(def temp-divider (format temp-format-str
                          "-------" "-------" "-------" "-------"
                          "------------"))

(defn temp-freq-formatter
  [fq]
  (format temp-format-str
          (:ansi fq)
          (:hex fq)
          (:count fq)
          (:percent fq)
          (:temp fq)))

(defn format-temp
  [k f]
  (format "%d K (%d F)" k f))

(defn add-temp-datum
  [data]
  (let [k (scales/temperature-amount biome/ts (:color-map data))
        f (int (util/to-fahrenheit k))]
    (assoc data :kelvin k
                :fahrenheit f
                :temp (format-temp k f))))

(defn add-temp-data
  [stats]
  (assoc stats :freqs (map add-temp-datum (:freqs stats))))

(defn get-temp-stats
  [im]
  (add-temp-data (get-stats im)))

(defn print-temps
  ([stats]
   (print-temps stats {:sort-by :count}))
  ([stats opts]
   (print-data stats temp-header temp-divider temp-freq-formatter opts)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Biome Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; XXX Create a reporter for biome data as well as generated biome images:
;;     * Generalize the logic in biome.core/set-biome-pixel! to work with both
;;       images and data structures, splitting it out into a supporting function
;;     * Refactor biome.core/set-biome-pixel! to use this new function
;;     * Might need to refactor biome.core/create-image, too
;;     * Generate stats for processed biome data:
;;       * Get counts of temp/precip combinations
;;       * Create printer function, output should include biome name/description
