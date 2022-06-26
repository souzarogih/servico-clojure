(ns servico-clojure.main
  (:require [servico-clojure.servidor :as servidor]
            [com.stuartsierra.component :as component]
            [servico-clojure.database :as database]
            [servico-clojure.rotas :as rotas])
  (:use [clojure.pprint]))


(defn my-component-system []
  (component/system-map
    :database (database/new-database)
    :rotas (rotas/new-rotas)
    :servidor (component/using (servidor/new-servidor) [:database :rotas])
    ))

(def component-result (component/start (my-component-system)))