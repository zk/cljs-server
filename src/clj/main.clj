(ns main
  (:gen-class)
  (:use [cljs-server.core]))

(defn -main [& opts]
  (let [opts (apply hash-map opts)]
    (start-server (Integer. (get opts :port "8080")))))
