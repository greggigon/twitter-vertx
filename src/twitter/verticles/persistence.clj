(ns twitter.verticles.persistence
  (:require [vertx.eventbus :as eb])
  (:import [java.util UUID])
  (:gen-class))


(def user-messages (ref {}))
(def all-messages (ref {}))
(def subscriptions (ref {}))
(def user-walls (ref {}))


(defn generate-id
  [user]
  (str user "_" (.toString (UUID/randomUUID))))

(defn save-message-for-user
  [user message]
  (let [message-id (generate-id user)
        new-entry (hash-map message-id {:message message :time (System/currentTimeMillis) :user user})
        persisted-message (dosync
                           (alter all-messages merge new-entry))]
   (dosync
    (alter user-messages assoc user
           (cons message-id
                 (get @user-messages user []))))
    message-id))

(defn get-messages-for-user
  [user]
  (->> (get @user-messages user)
       (map #(get @all-messages %))))


(defn subscribe
  [who to-whom]
  (let [user-subscriptions (get @subscriptions who [])]
    (dosync
     (alter subscriptions assoc who (cons to-whom user-subscriptions)))))

(defn contains-value? [coll value] (some #(= % value) coll))

(defn update-wall
  [user message-id]
  (let [followers (keys (filter #(contains-value? (val %) user) @subscriptions))]
    (loop [walls-to-update followers]
      (if (not (empty? walls-to-update))
        (let [user-wall (first walls-to-update)
              messages (get @user-walls user-wall)]
          (dosync (alter user-walls assoc (first walls-to-update) (cons message-id messages))))
        (recur (rest walls-to-update))))))

(defn save-handler
  [message]
  (do
    (println (str "Received persist new message " message))
    (let [message-id (save-message-for-user (:user message) (:message message))]
      (do
        (eb/reply message-id)
        (eb/publish "twitter.persistence.update_walls" {:user (:user message) :message message-id})))))

(defn read-handler
  [user]
  (do
    (println (str "Reading message for user [" user "]"))
    (->> (get-messages-for-user user)
         (eb/reply))))

(defn subscribe-handler
  [subscription]
  (do
    (println (str "Subscribing user " (:who subscription) " to follow user " (:to-whom subscription)))
    (subscribe (:who subscription) (:to-whom subscription))))

(defn update-walls-handler
  [message]
  (update-wall (:user message) (:message message)))

(defn read-wall-handler
  [user]
  (->> (get @user-walls user)
       (map #(get @all-messages %))
       (eb/reply)))


(eb/on-message "twitter.persistence.new_message" save-handler)

(eb/on-message "twitter.persistence.read_messages" read-handler)

(eb/on-message "twitter.persistence.subscribe" subscribe-handler)

(eb/on-message "twitter.persistence.update_walls" update-walls-handler)

(eb/on-message "twitter.persistence.read_wall" read-wall-handler)
