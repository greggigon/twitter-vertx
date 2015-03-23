(ns twitter.verticles.send
  (:require [vertx.eventbus :as eb])
  (:gen-class))

(defn send-message
  [message]
  (eb/send "twitter.persistence.new_message" message
           #(eb/publish "twitter.persistence.update_walls"
                                    {:user (:user message) :message %})))



(defn init
  []
  (println "stuff")
  (eb/on-message "twitter.client.send.address"
                 send-message
                 #(println (str "It looks like Send Vertx subscribed." %))))

(init)
