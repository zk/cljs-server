(ns widgets
  (:use html)
  (:require $
            _))

(def defaults {})

;; Tab Panel

(defn tab [title content]
  (let [
        link ($html [:a {:href "#"} title])]
    {:title link
     :content content
     :focus #(do (.addClass link "selected")
                 (.css content {:display "block"}))
     :blur #(do (.removeClass link "selected")
                (.css content {:display "none"}))
     :click #(.click link %)}))

(defn tab-panel [& tabs]
  (let [el ($html [:div {:class "tab-panel"}
                   [:div {:class "tabs"}
                    (map #(:title %) tabs)
                    [:div {:class "clear"}]]
                   [:div {:class "clear"}]
                   [:div {:class "tab-content"}
                    (map #(:content %) tabs)]])]
    (doseq [t tabs]
      (.click t (fn []
                  (map #(.blur %) tabs)
                  (.focus t)
                  false))
      (.blur t))
    (.focus (first tabs))
    el))

(defn panel []
  ($html [:div {:class "panel"}]))

(defn h-splitter []
  ($html [:div {:class "h-splitter"}]))

(defn size-h-split-pane [container left-el left-opts right-el]
  (let [w (.width container)
        h (.height container)
        left-width (or (:width left-opts) 200)]
    (.css left-el {:height h
                   :width left-width})
    (.css right-el {:height h
                    :width (- w left-width)})))

(defn h-split-pane [left right]
  (let [container (doto ($html [:div {:class "h-split-pane"}])
                    (.css {:width "100%"
                           :height "100%"}))
        left-el ($html [:div {:class "left-pane" :style "float: left; width: 100%; height: 100%;"}
                        (.css (:el left) {:width "100%"
                                          :height "100%"})])
        right-el ($html [:div {:class "right-pane" :style "float: left;"}
                         (.css (:el right) {:width "100%"
                                            :height "100%"})])]
    (.empty container)
    (util/append container left-el)
    (util/append container right-el)
    (util/append container ($html [:div {:style "clear: both"}]))
    (.resize container #(size-h-split-pane container left-el left right-el))
    (on-insert container #(size-v-split-pane container top-el bottom-el top))
    container))

(defn size-v-split-pane [container top-el bottom-el]
  (when (and container top-el bottom-el)
    (let [w (.outerWidth container)
          h (.outerHeight container)
          top-height (or (+ (.height top-el)) 100)
          bottom-height (- h top-height)]
      (.css top-el {:height top-height})
      (.css bottom-el {:height bottom-height}))))

(defn on-insert [el f]
  (.bind el "DOMNodeInserted"
         (fn []
           (when (aget (.parents el "body") 0)
             (f)))))

(defn v-split-pane [top bottom]
  (let [container (doto ($html [:div {:class "v-split-pane"}])
                    (.css {:width "100%"
                           :height "100%"}))
        top-el ($html [:div {:class "top-pane"}
                       (:el top)])
        bottom-el ($html [:div {:class "bottom-pane"}
                          (:el bottom)])]
    (.empty container)
    (util/append container top-el)
    (h-splitter)
    (util/append container bottom-el)
    (.resize container #(size-v-split-pane container top-el bottom-el top))
    (on-insert container #(size-v-split-pane container top-el bottom-el top))
    container))

