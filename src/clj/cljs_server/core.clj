(ns cljs-server.core
  (:use [net.cgrand.moustache :only (app)]
        [hiccup core [page-helpers :only (doctype)]]
        (nsfw util
              server
              render
              middleware
              [csrf :only (wrap-bind-csrf)])
        (ring.middleware file
                         file-info
                         params
                         nested-params
                         keyword-params
                         multipart-params
                         session)
        [ring.util.response]
        [ring.middleware.session.memory])
  (:require [somnium.congomongo :as mongo]
            [cljs.core2 :as cljs]))

;; Middleware

#_(defn wrap-user [f]
  (fn [req]
    (f (assoc req :user (user/current! req)))))

#_(defn require-user [f]
  (fn [req]
    (if (:user req)
      (f req)
      (redirect "/"))))

;;
;; Routing
;;

(def routes
  (app
   [""] (fn [req]
          (render
           :text
           (html (doctype :html5)
                 [:html
                  [:head
                   [:title "cljs-pad"]
                   (include-css :html5reset :app)
                   (include-js :jquery-1.4.2.min
                               :underscore.min
                               :jquery.ba-resize.min
                               "codemirror/codemirror.js"
                               "codemirror/mirrorframe.js"
                               :app)
                   [:body]]])))
   ["compile"] (fn [req]
                 (render :text (->> (:cljs-code (:params req))
                                    (cljs/compile-cljs-string))))
   ["render"] (fn [req]
                (try
                  (let [cljs-code (:cljs-code (:params req))
                        js (when cljs-code (cljs/compile-cljs-string cljs-code))
                        html (html (doctype :html5)
                                   [:html {:style "width: 100%; height: 100%;"}
                                    [:head
                                     (include-js :jquery-1.4.2.min :underscore.min :processing.min :stdlib)
                                     [:script {:type "text/javascript"}
                                      js]]
                                    [:body {:style "width: 100%; height: 100%; overflow: hidden; margin: 0px; padding: 0px;"}]])]
                    (render :text html))
                  (catch Exception e
                    (render :text (html [:h3 (.getMessage e)])))))))



(def sessions (atom {}))

(def entry-handler
  (-> (var routes)
      #_(wrap-user)
      #_(wrap-bind-csrf)
      (wrap-keyword-params)
      (wrap-nested-params)
      (wrap-params)
      (wrap-session {:store (memory-store sessions)})
      (wrap-multipart-params)
      (wrap-log-request)
      (wrap-file "resources/public")
      (wrap-file-info)
      (wrap-stacktrace)))

(defn start-server [port]
  (start (var entry-handler) port))
