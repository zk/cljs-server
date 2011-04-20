(defproject cljs-server "0.2.1"
  :description "A live-coding site for cljs."
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [ring "0.3.3"]
                 [net.cgrand/moustache "1.0.0-SNAPSHOT"]
                 [clj-stacktrace "0.2.0"]
                 [hiccup "0.3.0"]
                 [congomongo "0.1.3-SNAPSHOT"]
                 [org.clojars.hozumi/mongodb-session "1.0.0-SNAPSHOT"]
                 [org.danlarkin/clojure-json "1.2-SNAPSHOT"]
                 [nsfw "0.1-SNAPSHOT"]]
  :dev-dependencies [[swank-clojure "1.2.0"]
                     [marginalia "0.3.2"]
                     [cljs "0.2.1"]
                     [lein-cljs "0.2.1"]
                     [org.jclouds/jclouds-all "1.0-SNAPSHOT"]
                     [clj-ssh "0.2.0"]]
  :repositories {"jclouds-snapshot"
                 "https://oss.sonatype.org/content/repositories/snapshots"}    
  :source-path "src/clj"
  :cljs {:source-path "src/cljs"
         :source-output-path "resources/public/js"
         :source-libs [app]}
  :main main)
