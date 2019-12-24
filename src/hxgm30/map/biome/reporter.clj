(ns hxgm30.map.biome.reporter
  (:require
   [hxgm30.map.io :as map-io]
   [hxgm30.map.biome.core :as biome]
   [hxgm30.map.scales.core :as scales]
   [hxgm30.map.util :as util]))

(defn get-percent
  [fq total]
  (* 100 (float (/ fq total))))

(defn format-percent
  [p]
  (format "%.1f%%" p))

(defn format-precip
  [p]
  (format "%,d mm/yr" p))

(defn format-temp
  [k f]
  (format "%d K (%d F)" k f))

(defn get-frequency-data
  [total [rgb fq]]
  (let [color-map (util/rgb-pixel->color-map rgb)]
    {:count fq
     :percent (format-percent (get-percent fq total))
     :rgb rgb
     :color-map color-map
     :hex (util/clj-hex->html (util/rgb-pixel->hex rgb))
     :ansi (util/color-map->ansi color-map)}))

(defn add-precip-datum
  [data]
  (let [precip (scales/precipitation-amount biome/ps (:color-map data))]
    (assoc data :precip-raw precip
                :precip (format-precip precip))))

(defn add-precip-data
  [stats]
  (assoc stats :freqs (map add-precip-datum (:freqs stats))))

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

(defn get-stats
  [im]
  (let [rgbs-fqs (frequencies (sort (map-io/all-pixels im)))
        total (reduce + (vals rgbs-fqs))
        fdata (map (partial get-frequency-data total) rgbs-fqs)]
    {:freqs fdata
     :total total}))

(defn get-precip-stats
  [im]
  (add-precip-data (get-stats im)))

(defn get-temp-stats
  [im]
  (add-temp-data (get-stats im)))

(defn print-temps
  ([stats]
   (print-temps stats {:sort-by :count}))
  ([stats opts]
   (let [fqs (reverse (sort-by (:sort-by opts) (:freqs stats)))
         format-str "%7s%8s%8s%8s   %-12s"]
     (println (format format-str "Color" "Hex" "Count" "Percent" " Temperature"))
     (println (format format-str "-------" "-------" "-------" "-------" "------------"))
     (doall
      (for [fq fqs]
        (println (format format-str
                         (:ansi fq)
                         (:hex fq)
                         (:count fq)
                         (:percent fq)
                         (:temp fq)))))
     (println (format "\nTotal counts: %d" (:total stats))))
   :ok))
