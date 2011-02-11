(ns cljs-server.dev
  (:use [cljs-server.core]
        [nsfw.server]))

(do
  (stop)
  (start (var cljs-server.core/entry-handler)))
