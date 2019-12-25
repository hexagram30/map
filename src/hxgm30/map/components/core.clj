(ns hxgm30.map.components.core
  (:require
    [com.stuartsierra.component :as component]
    [hxgm30.map.components.config :as config]
    [hxgm30.map.components.layers :as layers]
    [hxgm30.map.components.logging :as logging]
    [taoensso.timbre :as log]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Common Configuration Components   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn cfg
  [cfg-data]
  {:config (config/create-component cfg-data)})

(def log
  {:logging (component/using
             (logging/create-component)
             [:config])})

(defn basic
  [cfg-data]
  (merge (cfg cfg-data)
         log))

(def map-image-layers
  {:layers (component/using
            (layers/create-component)
            [:config :logging])})

(def map-image-layers-without-logging
  {:layers (component/using
            (layers/create-component)
            [:config])})

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;   Component Initializations   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn initialize-bare-bones
  []
  (-> (config/build-config)
      basic
      component/map->SystemMap))

(defn initialize
  []
  (-> (config/build-config)
      basic
      (merge map-image-layers)
      component/map->SystemMap))

(defn initialize-for-tests
  []
  (-> (config/build-config)
      cfg
      (merge map-image-layers-without-logging)
      component/map->SystemMap))

(def init-lookup
  {:basic #'initialize-bare-bones
   :cli #'initialize-bare-bones
   :default #'initialize
   :testing #'initialize-for-tests})

(defn init
  ([]
    (init :default))
  ([mode]
    ((mode init-lookup))))

(def cli #(init :cli))