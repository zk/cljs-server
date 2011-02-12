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

(defn percent? [s]
  (and (string? s)
       (> (.indexOf s "%") -1)))

(defn parse-percent [s]
  (-> s
      (.replace "%" "")
      ('parseFloat)
      (/ 100)))

(defn size-h-split-pane [container left-el right-el opts]
  (let [w (.width container)
        h (.height container)
        split-pos (:pos (:splitter opts))
        split-width (:size (:splitter opts))
        left-width (cond
                    (percent? split-pos) (* w (parse-percent split-pos))
                    :else (or (:pos (:splitter opts)) 200))]
    (.css left-el {:height h
                   :width (- left-width
                             split-width)})
    (.css right-el {:height h
                    :width (- w left-width)})))

(defn v-splitter [container left-el right-el opts]
  (let [el (.css ($html [:div {:class "v-splitter"}])
                 {:width (or (:size opts) 10)
                  :height (.height container)
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
                                                      delta))))))
    (.mouseup ($ "body") (fn []
                           (set! dragging false)
                           (.remove shim)))
    el))

(defn h-split-pane [o]
  (let [split-opts (merge {:pos 200
                           :size 10
                           :dynamic false}
                          (:splitter o))
        opts (merge {:splitter split-opts}
                    o)
        container (doto ($html [:div {:class "h-split-pane"}])
                    (.css {:width "100%"
                           :height "100%"}))
        left-el ($html [:div {:class "left-pane" :style "float: left; width: 100%; height: 100%;"}
                        (.css (:left opts) {:width "100%"
                                            :height "100%"})])
        right-el ($html [:div {:class "right-pane" :style "float: left;"}
                         (.css (:right opts) {:width "100%"
                                              :height "100%"})])
        splitter (v-splitter container left-el right-el (:splitter opts))]
    (util/append container left-el)
    (when (:dynamic (:splitter opts))
      (util/append container splitter))
    (util/append container right-el)
    (util/append container ($html [:div {:style "clear: both"}]))
    (.resize ($ 'window) #(size-h-split-pane container left-el  right-el opts))
    
    (.resize ($ 'window) #(.height splitter (.height container)))
    (on-insert container #(.height splitter (.height container)))
    (on-insert container #(size-h-split-pane container left-el  right-el opts))
    container))

(defn on-insert [el f]
  (.bind el "DOMNodeInserted"
         (fn []
           (when (aget (.parents el "body") 0)
             (f)))))


(defn size-v-split-pane [container top-el bottom-el opts]
  (when (and container top-el bottom-el)
    (let [w (.outerWidth container)
          h (.outerHeight container)
          top-height (or (+ (.height top-el))
                         200)
          bottom-height (- h top-height (:splitter-height opts))]
      (.css top-el {:height top-height})
      (.css bottom-el {:height bottom-height}))))


(defn h-splitter [container top-el bottom-el opts]
  (let [el (.css ($html [:div {:class "h-splitter"}])
                 {:height (or (:splitter-height opts) 10)})
        dragging false
        last-y 0
        body ($ "body")
        shim (.css ($html [:div])
                   {:zIndex 9999
                    :width (.width body)
                    :height (.height body)
                    :backgroundColor "transparent"
                    :position "fixed"
                    :top 0
                    :left 0})]
    (.mousedown el (fn [e]
                     (set! dragging true)
                     (set! last-y e.clientY)
                     (.append body shim)))
    (.mousemove ($ "body") (fn [e]
                             (if dragging
                               (let [delta (- e.clientY last-y)]
                                 (set! last-y e.clientY)
                                 (.height top-el (+ (.height top-el)
                                                    delta))
                                 (.height bottom-el (- (.height bottom-el)
                                                       delta))))))
    (.mouseup ($ "body") (fn []
                           (set! dragging false)
                           (.remove shim)))
    el))

(defn v-split-pane [top bottom o]
  (let [opts (if o o {:splitter false
                      :splitter-height 0})
        container (doto ($html [:div {:class "v-split-pane"}])
                    (.css {:width "100%"
                           :height "100%"}))
        top-el ($html [:div {:class "top-pane"}
                       (:el top)])
        bottom-el ($html [:div {:class "bottom-pane"}
                          (:el bottom)])]
    (util/append container top-el)
    (if (:splitter opts)
      (util/append container (h-splitter container top-el bottom-el opts)))
    (util/append container bottom-el)
    (.resize container #(size-v-split-pane container top-el bottom-el opts))
    (on-insert container #(size-v-split-pane container top-el bottom-el opts))
    container))

