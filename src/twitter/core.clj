(ns twitter.core
  (:require [vertx.core :as core]
            [vertx.repl :as repl]
            [vertx.eventbus :as eb]
            [vertx.embed.platform :as platform]
            [vertx.embed :as embed]
            [vertx.http :as http]
            [vertx.http.route :as route]
            [clojure.data.json :as json]
            )
  (:gen-class))


(defn init-http-server
  []
  (-> (http/server)
      (http/on-request
       (-> (route/get "/message/:user"
                      (fn [req]
                        (let [params (http/params req)]
                          (eb/send "twitter.persistence.read_messages" (:user params)
                                   #(-> (http/server-response req {:status-code 200})
                                        (http/add-header "Content-Type" "application/json")
                                        (http/end (json/write-str %)))))))

           (route/post "/message/:user/:message"
                       (fn [req]
                         (let [params (http/params req)]
                           (eb/send "twitter.persistence.new_message" {:user (:user params) :message (:message params)}
                             #(-> (http/server-response req {:status-code 201})
                                  (http/add-header "Content-Type" "application/json")
                                  (http/end (json/write-str %)))))))
           (route/post "/follow/:who/:whom"
                       (fn [req]
                         (let [params (http/params req)]
                           (eb/send "twitter.persistence.subscribe" {:who (:who params) :to-whom (:whom params)})
                           (-> (http/server-response req {:status-code 201})
                               (http/end "Done")))))
           (route/get "/wall/:user"
                      (fn [req]
                        (let [params (http/params req)]
                          (eb/send "twitter.persistence.read_wall" (:user params)
                                   #(-> (http/server-response req {:status-code 200})
                                        (http/add-header "Content-Type" "application/json")
                                        (http/end (json/write-str %)))))))
           (route/no-match (fn [req]
                             (-> (http/server-response req {:status-code 405})
                                 (http/end "Not allowed"))))))
      (http/listen 8008 "localhost")))


(defn init
  []
  (let [pm (platform/platform-manager)]
    (do
      (embed/set-vertx! (embed/vertx))
      (platform/deploy-verticle pm "twitter/verticles/persistence.clj")
      (init-http-server)
      (println "Started ..."))))

(defn -main
  [& args]
  (init)
  (println "Hitting ENTER will stop the server ...")
  (line-seq (java.io.BufferedReader. *in*)))
