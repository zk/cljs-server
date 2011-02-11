(ns app
  (:use util html)
  (:require [widgets :as wd]
            CodeMirror
            MirrorFrame))

(defn control-panel []
  ($html [:div {:class "hello world"}
          [:h1 "Control Panel"]]))

(defn ajax [opts]
  (.ajax $ opts))

(defn on-save [cljs-ed js-ed]
  (ajax {:url "/compile"
         :type :POST
         :data {:cljs-code (.getCode cljs-ed)}
         :success (fn [resp]
                    (.setCode js-ed resp))})
  (let [form ($html [:form {:action "/render"
                            :id "iframe-form"
                            :method "POST"
                            :target "render-iframe"}
                     [:input {:type "text"
                              :name "cljs-code"
                              :style "display: none"
                              :value (.getCode cljs-ed)}]])]
    (.submit form)))

(def initial-content
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

"
  )
;; END INITIAL CONTENT

(ready
 (fn []
   (let [header ($html
                 [:header
                  [:h1 "CljsPad"]])
         content ($html [:div {:style "height: 100%;"}
                         (wd/h-split-pane
                                {:el ($html [:div {:class "cljs-editor-wrapper"}
                                             [:textarea {:id "cljs-editor"
                                                         :style ""}]])
                                 :width 600}
                                {:el (wd/v-split-pane
                                      {:el ($html [:div {:class "compiled-cljs-output"}
                                                   [:textarea {:id "js-editor"
                                                               :style ""}]])
                                       :height 400}
                                      {:el ($html [:iframe {:id "render-iframe"
                                                            :name "render-iframe"
                                                            :src "/render"
                                                            :style "height: 99.9%; width: 99.9%; margin: 0px; padding: 0px;"}])}
                                      {:splitter true
                                       :splitter-height 10})})])]
     (util/append ($ "body") (wd/v-split-pane
                              {:el header}
                              {:el content}))
     (let [js-output (.fromTextArea CodeMirror "js-editor" {:path "/js/codemirror/"
                                                            :height "100%"
                                                            :width "100%"
                                                            :parserfile ["parsejavascript.js"
                                                                         "tokenizejavascript.js"]
                                                            :stylesheet "/css/schemecolors.css"})
           cljs-editor (.fromTextArea CodeMirror "cljs-editor" {:path "/js/codemirror/"
                                                                :content initial-content
                                                                :height "100%"
                                                                :width "100%"
                                                                :parserfile ["parsescheme.js"
                                                                             "tokenizescheme.js"]
                                                                :stylesheet "/css/schemecolors.css"
                                                                :saveFunction #(on-save cljs-editor js-output)})
           ]))))






