(defproject cljs-server "1.0.0-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [ring "0.3.3"]
                 [net.cgrand/moustache "1.0.0-SNAPSHOT"]
                 [clj-stacktrace "0.2.0"]
                 [hiccup "0.3.0"]
                 [congomongo "0.1.3-SNAPSHOT"]
                 [org.clojars.hozumi/mongodb-session "1.0.0-SNAPSHOT"]
                 [org.danlarkin/clojure-json "1.2-SNAPSHOT"]
                 [nsfw "1.0.0-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.0"]
                     [marginalia "0.3.2"]
                     [cljs "0.0.1-SNAPSHOT"]]
  :source-path "src/clj"
  :cljs {:source-path "src/cljs"
         :output-path "resources/public/js"
         :libs [{:name app
                 :sources [util html widgets app]}]})