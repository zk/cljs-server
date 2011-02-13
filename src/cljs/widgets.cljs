(ns widgets
  (:use html)
  (:require $
            _))

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

(defn jquery? [o]
  (instanceof o 'jQuery))

(defn has-layout? [o]
  (when o
    (aget o :layout)))

(defn has-el? [o]
  (when o
    (aget o :el)))

(defn percent? [s]
  (and (string? s)
       (> (.indexOf s "%") -1)))

(defn parse-percent [s]
  (-> s
      (.replace "%" "")
      ('parseFloat)
      (/ 100)))

(defn css [el opts]
  (.css el opts))

(defn size-h-split-pane [container left-el right-el opts]
  (let [w (.width container)
        h (.height container)
        split-pos (or (:pos (:splitter opts))
                      200)
        split-width (or (:size (:splitter opts))
                        (if (:dynamic (:splitter opts))
                          10
                          0))
        left-width (cond
                    (percent? split-pos) (* w (parse-percent split-pos))
                    :else split-pos)]
    
    (css left-el {:height h
                  :width (- left-width
                            split-width)})
    (css right-el {:height h
                   :width (- w left-width)})))

(defn v-splitter [container left-el right-el opts]
  (let [el (.css ($html [:div {:class "v-splitter"}])
                 {:width (or (:size (:splitter opts))
                             (if (:dynamic (:splitter opts))
                               10
                               0))
                  :height (.height container)
                  :padding 0
                  :margin 0
                  :float "left"})
        dragging false
        last-x 0
        body ($ "body")
        shim (.css ($html [:div])
                   {:zIndex 9999
                    :width (.width body)
                    :height (.height body)
                    :backgroundColor "transparent"
                    :position "fixed"
                    :top 0
                    :left 0})]
    (when (:dynamic (:splitter opts))
      (.css el :cursor "col-resize")
      (.mousedown el (fn [e]
                       (set! dragging true)
                       (set! last-x e.clientX)
                       (.append body shim)))
      (.mousemove ($ "body") (fn [e]
                               (if dragging
                                 (let [delta (- e.clientX last-x)]
                                   (set! last-x e.clientX)
                                   (.width left-el (+ (.width left-el)
                                                      delta))
                                   (.width right-el (- (.width right-el)
                                                       delta))
                                   (when (has-layout? (:left opts))
                                     (.layout (:left opts)))
                                   (when (has-layout? (:right opts))
                                     (.layout (:right opts)))))))
      (.mouseup ($ "body") (fn []
                             (set! dragging false)
                             (.remove shim))))
    el))


(defn h-split-pane [o]
  (let [split-opts (merge {:pos 200
                           :size 10
                           :dynamic false}
                          (:splitter o))
        opts (merge {:splitter split-opts}
                    o)
        left-el (if (has-el? (:left opts))
                  (:el (:left opts))
                  (:left opts))
        right-el (if (has-el? (:right opts))
                   (:el (:right opts))
                   (:right opts))
        container (doto ($html [:div {:class "h-split-pane"}])
                    (.css {:width "100%"
                           :height "100%"}))
        left-pane ($html [:div {:class "left-pane" :style "float: left;  margin: 0px; padding: 0px; position: relative;"}
                          left-el])
        right-pane ($html [:div {:class "right-pane" :style "float: left; margin: 0px; padding: 0px; position: relative"}
                           right-el])
        splitter (v-splitter container left-pane right-pane opts)]
    (util/append container left-pane)
    (util/append container splitter)
    (util/append container right-pane)
    (util/append container ($html [:div {:style "clear: both"}]))
    {:el container
     :layout (fn []
               (.height splitter (.height container))
               (size-h-split-pane container left-pane right-pane opts)
               (when (has-layout? (:left opts))
                 (.layout (:left opts)))
               (when (has-layout? (:right opts))
                 (.layout (:right opts))))}))


(defn size-v-split-pane [container top-el bottom-el opts]
  (when (and container top-el bottom-el)
    (let [w (.outerWidth container)
          h (.outerHeight container)
          splitter-size (if (:dynamic opts)
                          (:size opts))
          top-height (:pos opts)
          bottom-height (- h top-height)]
      (.css top-el {:height top-height})
      (.css bottom-el {:height bottom-height}))))

(defn mk-shim [body]
  (css ($html [:div])
       {:zIndex 9999
        :width (.width body)
        :height (.height body)
        :backgroundColor "transparent"
        :position "fixed"
        :top 0
        :left 0}))


(defn h-splitter [container top-el bottom-el opts]
  (let [el ($html [:div {:class "h-splitter"}])
        dragging false
        last-y 0
        body ($ "body")
        shim (mk-shim body)]
    (.mousedown el (fn [e]
                     (set! dragging true)
                     (set! last-y e.clientY)
                     (.append body shim)))
    (.mousemove body (fn [e]
                       (if dragging
                         (let [delta (- e.clientY last-y)]
                           (set! last-y e.clientY)
                           (.height top-el (+ (.height top-el)
                                              delta))
                           (.height bottom-el (- (.height bottom-el)
                                                 delta))))))
    (.mouseup body (fn []
                     (set! dragging false)
                     (.remove shim)))
    el))


(defn v-split-pane [o]
  (let [split-opts (merge {:pos 300
                           :size 10
                           :dynamic false}
                          (:splitter o))
        opts (merge o {:splitter split-opts})
        container (doto ($html [:div {:class "v-split-pane"}])
                    (.css {:width "100%"
                           :height "100%"}))
        top-el (if (has-el? (:top opts))
                 (:el (:top opts))
                 (:top opts))
        bottom-el (if (has-el? (:bottom opts))
                    (:el (:bottom opts))
                    (:bottom opts))
        top-pane ($html [:div {:class "top-pane"
                               :style "position: relative;"}
                         top-el])
        bottom-pane ($html [:div {:class "bottom-pane"
                                  :style "position: relative"}
                            bottom-el])
        splitter (h-splitter container top-pane bottom-pane split-opts)]
    (util/append container top-pane)
    (if (:dynamic (:splitter opts))
      (util/append container splitter))
    (util/append container bottom-pane)
    {:el container
     :layout (fn []
               (.width splitter (.width container))
               (size-v-split-pane container top-pane bottom-pane split-opts)
               (when (has-layout? (:top opts))
                 (.layout (:top opts)))
               (when (has-layout? (:bottom opts))
                 (.layout (:bottom opts))))}))


