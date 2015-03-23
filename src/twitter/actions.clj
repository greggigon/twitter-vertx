(ns twitter.actions
  (:require [vertx.eventbus :as eb]
            [twitter.verticles.persistence :as p]))


(defn twitter-send
  [who message]
  (eb/send "twitter.client.send.address"
              {:user who :message message}
              #(println (str "Message to [" who "] sent"))))


(defn twitter-read
  [who]
  (p/get-messages-for-user who))


(defn twitter-subscribe
  [who to-whom]
  (eb/send "twitter.persistence.subscribe" {:who who :to-whom to-whom}))

(defn twitter-wall
  [for-whom]
  (->> (p/read-wall-handler for-whom)
       (map #(str "> " (:user %)" - " (:message %)))))
