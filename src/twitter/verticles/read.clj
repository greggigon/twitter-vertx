(ns twitter.verticles.read
  (:require [vertx.eventbus :as eb]))


(defn read-message-for-user
  [user]
  (eb/send "twitter.persistence.read_messages" user
           #(do (println (str "Received response with" %))
                (eb/reply %))))

(defn init
  []
  (eb/on-message "twitter.client.read.address"
                 read-message-for-user
                 #(println (str "Manage to subscribe Read Vertx" %))))

(init)
