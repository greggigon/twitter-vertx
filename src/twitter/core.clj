(ns twitter.core
  (:require [vertx.core :as core]
            [vertx.repl :as repl]
            [vertx.eventbus :as eb]
            [vertx.embed.platform :as platform]
            [vertx.embed :as embed]
            [vertx.http :as http]
            )
  (:gen-class))


(defn init-http-server
  []
  (-> (http/server)
      (http/on-request #(println (str "Received request -> " %)))
      (http/listen 8008 "localhost")))


(defn init
  []
  (let [pm (platform/platform-manager)]
    (do
      (embed/set-vertx! (embed/vertx))
      (platform/deploy-verticle pm "twitter/verticles/send.clj")
      (platform/deploy-verticle pm "twitter/verticles/persistence.clj")
      (platform/deploy-verticle pm "twitter/verticles/read.clj")
      (init-http-server)
      (println "Started ..."))))

(init)
