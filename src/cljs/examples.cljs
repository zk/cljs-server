(ns examples)


(def canvas
  "(ns cljspad
    (:use util))

(def elem (doto (.createElement 'document \"canvas\")
            (aset :width 500)
            (aset :height 500)))

(def ctx (.getContext elem \"2d\"))

(defn next-step [pos dir]
  (fn []
    (.rotate ctx 15)
    (set! ctx.fillStyle \"rgba(0,0,0,0.05)\")
    (.fillRect ctx 0 0 elem.width elem.height)

    (set! ctx.fillStyle \"rgba(255,0,0,1)\")
    (.fillRect ctx pos pos 20 20)

    (let [next-pos (+ pos dir)]
      (cond
       (> next-pos elem.width) ('setTimeout (next-step next-pos -1) 10)
       (< (+ next-pos 20) 0) ('setTimeout (next-step next-pos 1) 10)
       :else ('setTimeout (next-step next-pos dir) 10)))))

(ready
 #(do
    (.appendChild 'document.body elem)
    (.fillRect ctx 0 0 elem.width elem.height)
    ('setTimeout
     (next-step 0 1)
     10)))

;; Adapted from http://ejohn.org/apps/spiral/canvas.html

")

(def empty "(ns cljspad)\n")

(def dom
 "(ns cljspad-dom
  (:use util html)
  (:require [jQuery :as $]))

(defn rand []
  (.random 'Math))

(defn random-color []
  (str
   \"rgba(\"
   (.floor 'Math (* 255 (rand)))
   \",\"
   (.floor 'Math (* 255 (rand)))
   \",\"
   (.floor 'Math (* 255 (rand)))
   \",\"
   (+ (rand) 0.5)
   \")\"))

(defn move-el [el body]
  (let [new-x (* (.width body)
                 (rand))
        new-y (* (.height body)
                 (rand))]
    (println (random-color))
    (.css el {:background (random-color)})
    (.animate el {:top new-y
                  :left new-x})))

(defn make-el [body]
  (let [el ($html [:div \"mouseover me!\"])]
    (.css el {:backgroundColor \"red\"
              :height 100
              :width 100
              :margin \"10px\"
              :position \"absolute\"
              :color \"white\"
              :padding \"10px\"})
    (.mouseover el #(move-el el body))))

(ready
 (fn []
   (let [body ($ \"body\")]
     (.css body {:backgroundImage \"url('/images/dombg.jpg')\"})
     (append body (make-el body)))))
")

(def tpl
"(ns cljspad-templating
  (:use util html))

(defn split [del s]
  (.split s del))

(ready
 (fn []
   (append
    ($ \"body\")

    ($html [:div {:style \"padding: 10px;\"}
            [:h1 \"Hello World!\"]
            [:p \"The quick brown fox jumps over the lazy dog.\"]
            [:ul
             (->> \"Lorem Ipsum Dolor Sit Amet\"
                  (split \" \")
                  (map #($html [:li %])))]]))))")
