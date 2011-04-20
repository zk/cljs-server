(ns util
  (:import [jQuery :as $]))

(defn string? [o]
  (and o (= el.constructor String)))

(defn array? [el]
  (and el (= el.constructor Array)))

(defn has-el? [o]
  (when o
    (aget o :el)))

(defn append [p c]
  (cond
   (array? c) (map (fn [c] (append p c)) c)
   (has-el? c) (append p (:el c))
   :else (do
           (.append p c)
           (when (instanceof c 'jQuery)
             (.trigger c "postinsert"))))
  p)

(defn ready [f]
  (.ready ($ 'document) f))


