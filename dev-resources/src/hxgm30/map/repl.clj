(ns hxgm30.map.repl
  (:require
    [clojure.data.avl :as avl]
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.string :as string]
    [clojure.tools.namespace.repl :as repl]
    [clojusc.system-manager.core :refer :all]
    [clojusc.twig :as logger]
    [com.evocomputing.colors :as colors]
    [com.stuartsierra.component :as component]
    [hxgm30.map.bands :as bands]
    [hxgm30.map.biome.core :as biome]
    [hxgm30.map.biome.precipitation :as biome-precip]
    [hxgm30.map.biome.reporter :as reporter]
    [hxgm30.map.biome.temperature :as biome-temp]
    [hxgm30.map.components.config :as config]
    [hxgm30.map.components.core]
    [hxgm30.map.components.layers :as layers]
    [hxgm30.map.io :as map-io]
    [hxgm30.map.row :as row]
    [hxgm30.map.scales.core :as scales]
    [hxgm30.map.scales.elevation :as elev-scale]
    [hxgm30.map.scales.precipitation :as precip-scale]
    [hxgm30.map.scales.temperature :as temp-scale]
    [hxgm30.map.scales.util :as scale-util]
    [hxgm30.map.tile :as tile]
    [hxgm30.map.util :as util]
    [hxgm30.map.voronoi :as voronoi]
    [taoensso.timbre :as log]
    [trifl.java :refer [show-methods]])
  (:import
    (javax.imageio ImageIO)
    (java.net URI)
    (java.nio.file Paths)
    (org.apache.commons.math3.util FastMath)
    (org.davidmoten.hilbert HilbertCurve
                            HilbertCurve$Builder
                            HilbertCurveRenderer
                            HilbertCurveRenderer$Option)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Initial Setup & Utility Functions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def setup-options {
  :init 'hxgm30.map.components.core/init
  :after-refresh 'hxgm30.map.repl/init-and-startup
  :throw-errors false})

(defn init
  "This is used to set the options and any other global data.

  This is defined in a function for re-use. For instance, when a REPL is
  reloaded, the options will be lost and need to be re-applied."
  []
  (logger/set-level! '[hxgm30] :info)
  (setup-manager setup-options))

(defn init-and-startup
  "This is used as the 'after-refresh' function by the REPL tools library.
  Not only do the options (and other global operations) need to be re-applied,
  the system also needs to be started up, once these options have be set up."
  []
  (init)
  (startup))

;; It is not always desired that a system be started up upon REPL loading.
;; Thus, we set the options and perform any global operations with init,
;; and let the user determine when then want to bring up (a potentially
;; computationally intensive) system.
(init)

(defn banner
  []
  (println (slurp (io/resource "text/banner.txt")))
  :ok)

(def hcb (.bits (HilbertCurve/small) 4))
(def hc (.dimensions hcb 2))

(comment
  (HilbertCurveRenderer/renderToFile
    4 400 "hilbert.png" (into-array HilbertCurveRenderer$Option []))
  (HilbertCurveRenderer/renderToFile
    4 1200 "hilbert.png" (into-array HilbertCurveRenderer$Option
                                     [HilbertCurveRenderer$Option/COLORIZE
                                      HilbertCurveRenderer$Option/LABEL]))
  (.query hc (long-array [3 3]) (long-array [8 10])))

(comment
  (util/make-matrix biome/sorted-temps biome/sorted-precips)
  (biome/print-colors-matrix)
  (biome/print-legend)
  (biome/create-image)
  (scales/print-colors biome-temp/tr)
  (def ts (scales/new-scale :temperature :sine))
  (scales/get-ticks ts)
  (def ps (scales/new-scale :precipitation :exponential 2))
  (scales/get-ticks ps)
  ;; XXX I think maybe these functions should be moved into a new
  ;;     ns for reporting ... maybe hxgm30.map.biome.reports
  ;; Show frequencies of precipitations
  (def im (biome-precip/read-precipitation-tiny))
  (def f (frequencies (sort (map-io/all-pixels im))))
  (def tot (reduce + (vals f)))
  (def sorted (sort (map (fn [[c fq]]
                        (let [color-map (util/rgb-pixel->color-map c)
                              percent (Math/round (* 100 (float (/ fq tot))))
                              precip (precip-scale/precipitation-amount biome/ps color-map)]
                          [fq
                           (format "%,d mm/yr" precip)
                           fq
                           (format "%d%%" percent)
                           (util/color-map->ansi color-map)
                           (util/clj-hex->html (util/rgb-pixel->hex c))
                           ]))
                      f)))
  (reverse (map rest sorted))
  ;; ordered by precipitation
  (def sorted
    (sort (map (fn [[c fq]]
                (let [color-map (util/rgb-pixel->color-map c)
                      percent (Math/round (* 100 (float (/ fq tot))))
                      precip (precip-scale/precipitation-amount biome/ps color-map)]
                  [precip
                   (format "%,d mm/yr" precip)
                   fq
                   (format "%d%%" percent)
                   (util/color-map->ansi color-map)
                   (util/clj-hex->html (util/rgb-pixel->hex c))]))
              f)))
  (reverse (map rest sorted))
  ;; Show frequencies of temperatures
  ;; XXX there's a bug here somewhere: the hottest color is converted to a
  ;;     temp of 0 K ... need to write some unit tests for
  ;;     temp-scale/temperature-amount
  (def im (biome-temp/read-temperature-tiny))
  (def stats (reporter/get-temp-stats im))
  (reporter/print-temps stats) ; default is to order by highest counts
  (reporter/print-temps stats {:sort-by :kelvin})
  ;; XXX let's get a report of all the biomes, too ...
  ;;     can the code in biome.core/set-biome-pixel! be generealized for both
  ;;     image and non-image use?
  )
