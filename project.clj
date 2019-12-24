(defn get-banner
  []
  (try
    (str
      (slurp "resources/text/banner.txt")
      ;(slurp "resources/text/loading.txt")
      )
    ;; If another project can't find the banner, just skip it;
    ;; this function is really only meant to be used by Dragon itself.
    (catch Exception _ "")))

(defn get-prompt
  [ns]
  (str "\u001B[35m[\u001B[34m"
       ns
       "\u001B[35m]\u001B[33m λ\u001B[m=> "))

(defproject hexagram30/map "0.2.0-SNAPSHOT"
  :description "A map-making library for worlds, cities, dungeons, and buildings in hexagram30 projects"
  :url "https://github.com/hexagram30/map"
  :license {
    :name "Apache License, Version 2.0"
    :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :exclusions [
    ;; JDK version issues overrides
    [org.clojure/clojure]
    [org.clojure/core.rrb-vector]
    [org.clojure/tools.reader]]
  :dependencies [
    ;; JDK version issues overrides
    [org.clojure/core.rrb-vector "0.1.1"]
    [org.clojure/tools.reader "1.3.2"]
    ;; Regular dependencies
    [clojusc/system-manager "0.3.0"]
    [clojusc/twig "0.4.1"]
    [com.evocomputing/colors "1.0.4"]
    [hexagram30/common "0.1.0-SNAPSHOT"]
    [org.apache.commons/commons-math3 "3.6.1"]
    [org.clojure/clojure "1.10.1"]
    [org.clojure/data.avl "0.1.0"]
    [org.flatland/ordered "1.5.7"]
    [trystan/delaunay-triangulation "1.0.1"]
    [trystan/voronoi-diagram "1.0.0"]]
  :profiles {
    :ubercompile {
      :aot :all}
    :dev {
      :dependencies [
        [clojusc/trifl "0.4.2"]
        [com.github.davidmoten/hilbert-curve "0.2.1"]
        [org.clojure/tools.namespace "0.3.1"]]
      :plugins [
        [lein-shell "0.5.0"]
        [venantius/ultra "0.6.0"]]
      :source-paths ["dev-resources/src"]
          :resource-paths ["assets/images"]
      :repl-options {
        :init-ns hxgm30.map.repl
        :prompt ~get-prompt
        :init ~(println (get-banner))}}
    :lint {
      :source-paths ^:replace ["src"]
      :test-paths ^:replace []
      :plugins [
        [jonase/eastwood "0.3.6"]
        [lein-ancient "0.6.15"]
        [lein-kibit "0.1.8"]
        [lein-nvd "1.3.1"]]}
    :test {
      :plugins [
        [lein-ltest "0.4.0"]]
      :test-selectors {
        :unit #(not (or (:integration %) (:system %)))
        :integration :integration
        :system :system
        :default (complement :system)}}}
  :aliases {
    ;; Dev Aliases
    "repl" ["do"
      ["clean"]
      ["repl"]]
    "ubercompile" ["do"
      ["clean"]
      ["with-profile" "+ubercompile" "compile"]]
    "check-vers" ["with-profile" "+lint" "ancient" "check" ":all"]
    "check-jars" ["with-profile" "+lint" "do"
      ["deps" ":tree"]
      ["deps" ":plugin-tree"]]
    "check-deps" ["do"
      ["check-jars"]
      ["check-vers"]]
    "kibit" ["with-profile" "+lint" "kibit"]
    "eastwood" ["with-profile" "+lint" "eastwood" "{:namespaces [:source-paths]}"]
    "lint" ["do"
      ["kibit"]
      ; ["eastwood"]
      ]
    "ltest" ["with-profile" "+test" "ltest"]
    "ltest-clean" ["do"
      ["clean"]
      ["ltest"]]
    "build" ["do"
      ["clean"]
      ["check-vers"]
      ["lint"]
      ["ltest" ":all"]
      ["ubercompile"]
      ["jar"]]})
