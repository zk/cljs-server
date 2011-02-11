(ns util
  (:require $))

(defn string? [o]
  (and o (= el.constructor String)))

(defn array? [el]
  (and el (= el.constructor Array)))

(defn append [p c]
  (if (array? c)
    (map (fn [c] (append p c)) c)
    (do
      (.append p c)
      (when (instanceof c 'jQuery)
        (.trigger c "postinsert"))))
  p)

(defn ready [f]
  (.ready ($ 'document) f))


