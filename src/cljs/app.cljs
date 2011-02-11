(ns app
  (:use util html)
  (:require [widgets :as wd]
            CodeMirror
            MirrorFrame))

(defn control-panel []
  ($html [:div {:class "hello world"}
          [:h1 "Control Panel"]]))

(ready
 (fn []
   (let [header ($html
                 [:header
                  [:h1 "CljsPad"]
                  [:div {:id "content"}]])
         content ($html [:div {:style "background-color: green; height: 100%;"}
                         (wd/h-split-pane
                          {:el (control-panel)}
                          {:el ($html [:div {:class "cljs-editor-wrapper"}
                                       [:textarea {:id "cljs-editor"
                                                   :style ""}
                                        "(ns cljspad)"]])})])]
     (.css ($ "body") {:backgroundColor "orange"})
     (util/append ($ "body") (wd/v-split-pane
                              {:el header}
                              {:el content}))
     #_(.fromTextArea CodeMirror "html-editor" {:path "/js/codemirror/"
                                                :content (.val ($ "#cljs-editor"))
                                                :height "100%"
                                                :width "100%"
                                                :parserfile ["parsescheme.js"
                                                             "tokenizescheme.js"]
                                                :stylesheet "/css/schemecolors.css"})
     (.fromTextArea CodeMirror "cljs-editor" {:path "/js/codemirror/"
                                              :content (.val ($ "#cljs-editor"))
                                              :height "100%"
                                              :width "100%"
                                              :parserfile ["parsescheme.js"
                                                           "tokenizescheme.js"]
                                              :stylesheet "/css/schemecolors.css"}))))






