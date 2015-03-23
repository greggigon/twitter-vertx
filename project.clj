(defproject twitter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [io.vertx/vertx-core "2.1.5"]
                 [io.vertx/vertx-platform "2.1.5"]
                 [io.vertx/clojure-api "1.0.4"]
                 [lein-light-nrepl "0.1.0"]]

  :plugins [[lein-vertx "0.3.1"]]
  :vertx {:main twitter.core/init
          :author "Greg Gigon"
          :keywords ["functional" "reactive" "twitter"]}
  :profiles {:dev {:plugins [[cider/cider-nrepl "0.8.2"]]}}
  :main twitter.core
  :repl-options {:nrepl-middleware [lighttable.nrepl.handler/lighttable-ops]})
