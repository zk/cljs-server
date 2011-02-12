(ns app
  (:use util html)
  (:require [widgets :as wd]
            CodeMirror
            MirrorFrame
            examples))

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

(def cljs-editor 'null)

(defn seed-link [link-text code]
  (doto ($html [:a {:href "#"} link-text])
    (.click (fn []
              (.setCode cljs-editor code)
              false))))

(ready
 (fn []
   (let [header ($html
                 [:header
                  [:h1 "CljsPad"]])
         content ($html [:div {:style "height: 100%;"}
                         (wd/h-split-pane
                          {:left (.css ($html [:div {:class "left-bar"}
                                               [:div {:class "left-content"}
                                                [:p "CljsPad is an live console for the experimental "
                                                 [:a {:href "http://clojure.org"} "clojure"]
                                                 "(ish)-to-javscript compiler, "
                                                 [:a {:href "http://github.com/zkim/cljs"} "cljs"]
                                                 "."]
                                                [:br]
                                                [:p "Place your cursor in the left pane, hit ctrl+s, and the cljs code will be transformed into javscript (top-right panel), executed, and the result displayed in the bottom-right panel."]
                                                [:br]
                                                [:h4 "Examples"]
                                                [:ul
                                                 [:li
                                                  (seed-link "empty" examples/empty)]
                                                 [:li
                                                  (seed-link "html5 canvas" examples/canvas)]
                                                 [:li
                                                  (seed-link "jquery dom" examples/dom)]
                                                 [:li
                                                  (seed-link "templating" examples/tpl)]]]])
                                       {:backgroundColor "#eee"
                                        :borderRight "solid black 1px"
                                        :zIndex 5000})
                           :right (wd/h-split-pane
                                   {:left ($html [:div {:class "cljs-editor-wrapper"}
                                                  [:textarea {:id "cljs-editor"
                                                              :style ""}]])
                                    :right (wd/v-split-pane
                                            {:el ($html [:div {:class "compiled-cljs-output"}
                                                         [:textarea {:id "js-editor"
                                                                     :style ""}]])
                                             :height 400}
                                            {:el ($html [:iframe {:id "render-iframe"
                                                                  :name "render-iframe"
                                                                  :src "/render"
                                                                  :style "height: 99.9%; width: 99.9%; margin: 0px; padding: 0px;"}])}
                                            {:splitter true
                                             :splitter-height 10})
                                    :splitter {:pos "50%"
                                               :size 10
                                               :dynamic true}})
                           :splitter {:pos 200
                                      :size 5
                                      :dynamic false}})])]
     (util/append ($ "body") (wd/v-split-pane
                              {:el header}
                              {:el content}))
     (let [js-output (.fromTextArea CodeMirror "js-editor" {:path "/js/codemirror/"
                                                            :height "100%"
                                                            :width "100%"
                                                            :parserfile ["parsejavascript.js"
                                                                         "tokenizejavascript.js"]
                                                            :stylesheet "/css/jscolors.css"})
           cljs-ed (.fromTextArea CodeMirror "cljs-editor" {:path "/js/codemirror/"
                                                            :content examples/canvas
                                                            :height "100%"
                                                            :width "100%"
                                                            :parserfile ["parsescheme.js"
                                                                         "tokenizescheme.js"]
                                                            :stylesheet "/css/schemecolors.css"
                                                            :saveFunction #(on-save cljs-ed js-output)})]
       (set! cljs-editor cljs-ed)))))

