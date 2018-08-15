(ns hxgm30.map.repl
  (:require
    [clojure.java.io :as io]
    [clojure.pprint :refer [pprint]]
    [clojure.string :as string]
    [clojure.tools.namespace.repl :as repl]
    [clojusc.system-manager.core :refer :all]
    [clojusc.twig :as logger]
    [com.stuartsierra.component :as component]
    [hxgm30.map.components.config :as config]
    [hxgm30.map.components.core]
    [hxgm30.map.components.layers :as layers]
    [hxgm30.map.io :as map-io]
    [trifl.java :refer [show-methods]])
  (:import
    (javax.imageio ImageIO)
    (java.net URI)
    (java.nio.file Paths)))

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
  (logger/set-level! '[hxgm30] :debug)
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


(comment

  ;; Land colors
  (config/land? (system) {:blue 30 :green 120 :red 196})
  (config/land? (system) {:blue 34 :green 136 :red 221})
  (config/land? (system) {:blue 22 :green 90 :red 147})
  (config/sea? (system) {:blue 22 :green 90 :red 147})
  ;; Sea colors
  (config/land? (system) {:blue 119 :green 51 :red 0})
  (config/sea? (system) {:blue 119 :green 51 :red 0})
  (config/sea? (system) {:blue 115 :green 49 :red 0})
  ;; Ice colors
  (config/ice? (system) {:blue 255 :green 255 :red 255})
  (config/ice? (system) {:blue 223 :green 223 :red 223})
  (config/ice? (system) {:blue 119 :green 51 :red 0})
  (config/ice? (system) {:blue 22 :green 90 :red 147})
  )
