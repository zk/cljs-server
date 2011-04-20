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
            [cljs.core2 :as cljs]
            [org.danlarkin.json :as json]))

;; Middleware

#_(defn wrap-user [f]
  (fn [req]
    (f (assoc req :user (user/current! req)))))

#_(defn require-user [f]
  (fn [req]
    (if (:user req)
      (f req)
      (redirect "/"))))

(defn new-bson-id []
  (org.bson.types.ObjectId.))

(defn bson-id [o]
  (org.bson.types.ObjectId/massageToObjectId o))

(defn bson-id-encoder
  [bson-id writer pad current-indent start-token-indent indent-size]
  (.append writer (str start-token-indent \" (str bson-id) \")))

(json/add-encoder org.bson.types.ObjectId bson-id-encoder)



;;
;; Routing
;;

(defn index [code-id]
  (fn [req]
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
             [:script {:type "text/javascript"}
              "var CODE = "
              (if code-id
                (json-encode (:code (mongo/fetch-one :code :where {:_id (bson-id code-id)})))
                "null")
              ";"
              "var CODE_ID = \"" (str code-id) "\";"
              "var RECENT = " (json-encode (filter #(and (identity (:_id %))
                                                         (identity (:code %))) (mongo/fetch :code :limit 10)))
              ";"]
             [:body]]]))))

(def routes
  (app
   [""] (index nil)
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
                    (render :text (html [:h3 (.getMessage e)])))))
   ["save"] (fn [req]
              (let [cljs-code (:cljs-code (:params req))
                    id (:id (:params req))]
                (if (not (empty? id))
                  (do
                    (mongo/update! :code
                                   {:_id (bson-id id)}
                                   {:_id (bson-id id)
                                    :code cljs-code})
                    (render :json {:success true :id id}))
                  (render :json {:success true :id (str (:_id (mongo/insert! :code {:_id (new-bson-id)
                                                                                    :code
                                                                                    cljs-code})))}) )))
   ["c" code-id] (index code-id)
   ["client" "compile"] (fn [req]
                          (try
                            (let [code (:code (json-decode (slurp (:body req))))]
                              (render :json {:js (cljs/compile-cljs-string code)}))
                            (catch Exception e
                              (-> (response (.getMessage e))
                                  (status 500)
                                  (header "Content-Type" "text/html")))))))

(mongo/mongo! :db :cljs-server)

(def sessions (atom {}))

(def entry-handler
  (-> (var routes)
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
