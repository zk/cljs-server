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
(def js-output 'null)

(defn seed-link [link-text code]
  (doto ($html [:a {:href "#"} link-text])
    (.click (fn []
              (.setCode cljs-editor code)
              false))))

(defn bstuff [color content]
  (.css ($html [:div {:style (str "background-color: " color)}
                content])
        {:position "absolute"
         :top 0
         :bottom 0
         :left 0
         :right 0
         :padding "20px"
         :border "solid black 1px"}))



#_(ready
 (fn []
   (let [split1 (wd/v-split-pane
                 {:top ($html [:div {:style "border-bottom: solid black 1px;"}
                               "foo"])
                  :splitter {:pos 17}
                  :bottom (wd/h-split-pane
                         {:left (bstuff "green" "left")
                          :right (wd/h-split-pane
                                  {:left (bstuff "orange" "left2")
                                   :right (wd/v-split-pane
                                           {:top (bstuff "blue" "top")
                                            :bottom (bstuff "red" "bottom")})})
                          :splitter {:pos 150
                                     :dynamic true}})})]
     (util/append ($ "body")
                  (:el split1))
     (.layout split1)
     (.resize ($ 'window) (fn []
                            (.layout split1))))))

(defn main-split [])

(defn sidebar []
  (.css ($html [:div {:class "left-bar"}
                [:div {:class "left-content"}
                 [:p "CljsPad is an live console for the experimental "
                  [:a {:href "http://clojure.org"} "clojure"]
                  "(ish)-to-javscript compiler, "
                  [:a {:href "http://github.com/zkim/cljs"} "cljs"]
                  "."]
                 [:br]
                 [:p
                  [:b "Place your cursor in the left pane, hit ctrl+s"]
                  ", and the cljs code will be transformed into javscript (top-right panel), executed, and the result displayed in the bottom-right panel."]
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
         :height "100%"
         :zIndex 5000}))

(defn sidebar-split []
  (wd/h-split-pane
   {:left (sidebar)
    :right (wd/h-split-pane
            {:left ($html [:div {:class "cljs-editor-wrapper"}
                           [:textarea {:id "cljs-editor"
                                       :style ""}]])
             :right (wd/v-split-pane
                     {:top ($html [:div {:class "compiled-cljs-output"}
                                   [:textarea {:id "js-editor"}]])
                      :bottom (.css ($html [:iframe {:id "render-iframe"
                                                     :name "render-iframe"
                                                     :src "/render"}])
                                    {:height "100%"
                                     :width "100%"})
                      :splitter {:pos 300
                                 :dynamic true}})
             :splitter {:pos "50%"
                        :size 10
                        :dynamic true}})
    :splitter {:pos 200
               :size 0
               :dynamic false}}))


(defn save []
  (ajax {:url "/save"
         :type "POST"
         :data {:cljs-code (.getCode cljs-editor)
                :id 'CODE_ID}
         :dataType "json"
         :success (fn [resp]
                    (println resp)
                    (if resp.success
                      (set! 'location.href (str "/c/" resp.id))))}))


(defn main-layout [header content]
  (wd/v-split-pane
   {:top header
    :bottom content
    :splitter {:pos 39}}))

(defn new-button []
  (doto ($html [:button {:class "myButton"}
                "new"])
    (.click #(set! 'location.href "/"))))

(defn save-button []
  (let [el ($html [:button {:class "myButton"}
                   (if 'CODE
                     "update"
                     "save")])]
    (.click el #(save))))

(defn run-button []
  (let [el ($html [:button {:class "myButton"} "run"])]
    (.click el #(on-save cljs-editor js-output))))

(defn header []
  ($html
   [:header
    [:a {:href "/"}
     [:h1 "CljsPad"]]
    [:div {:class "buttons"}
     (new-button)
     (run-button)
     (save-button)]]))

(ready
 (fn []
   (let [main (main-layout
               (header)
               (sidebar-split))]
     (util/append ($ "body") main)
     (let [js-out (.fromTextArea CodeMirror "js-editor" {:path "/js/codemirror/"
                                                         :height "100%"
                                                         :width "100%"
                                                         :parserfile ["parsejavascript.js"
                                                                      "tokenizejavascript.js"]
                                                         :stylesheet "/css/jscolors.css"})
           cljs-ed (.fromTextArea CodeMirror "cljs-editor" {:path "/js/codemirror/"
                                                            :content (if 'CODE
                                                                       'CODE
                                                                       examples/empty)
                                                            :height "100%"
                                                            :width "100%"
                                                            :autoMatchParens true
                                                            :parserfile ["parsescheme.js"
                                                                         "tokenizescheme.js"]
                                                            :stylesheet "/css/schemecolors.css"
                                                            :saveFunction #(on-save cljs-ed js-out)})]
       
       (set! cljs-editor cljs-ed)
       (set! js-output js-out)
       (.layout main)
       (.resize ($ 'window) (fn []
                              (.layout main)))))))

