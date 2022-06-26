(ns servico-clojure.rotas
  (:require [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]
            [com.stuartsierra.component :as component]
            [io.pedestal.http.body-params :as bodyparam]
            [cheshire.core :as json]))

(defrecord Rotas []
  component/Lifecycle

  (start [this]

    (println "Start rotas")

    (defn listar-tarefas [request]
      {:status 200 :body @(:store request)})

    (defn criar-tarefa-mapa [uuid nome status]
      {:id uuid :nome nome :status status})

    (defn criar-tarefa [request]
      (let [uuid (java.util.UUID/randomUUID)
            nome (get-in request [:query-params :nome])
            status (get-in request [:query-params :status])
            body (get request :json-params)
            _ (println "\nbody:" (get-in (bodyparam/json-parser request) [:json-params]))
            tarefa (criar-tarefa-mapa uuid nome status)
            store (:store request)]
        (swap! store assoc uuid tarefa)
        {:status 200 :body {:mensagem "Tarefa adicionada com sucesso!"
                            :tarefa   tarefa}}))

    (defn funcao-hello [request]
      {:status 200 :body (str "Hello World " (get-in request [:query-params :name] "Everybady"))})

    (defn remover-tarefa [request]
      (let [store (:store request)
            tarefa-id (get-in request [:path-params :id])
            tarefa-id-uuid (java.util.UUID/fromString tarefa-id)]
        (swap! store dissoc tarefa-id-uuid)
        {:status 200 :body {:menssagem "Removida com sucesso"}}))

    (defn atualizar-tarefa [request]
      (let [tarefa-id (get-in request [:path-params :id])
            tarefa-id-uuid (java.util.UUID/fromString tarefa-id)
            nome (get-in request [:query-params :nome])
            status (get-in request [:query-params :status])
            tarefa (criar-tarefa-mapa tarefa-id-uuid nome status)
            store (:store request)]
        (swap! store assoc tarefa-id-uuid tarefa)
        {:status 200 :body {:mensagem "Tarefa atualizada com sucesso!"
                            :tarefa   tarefa}}))

    (defn teste []
      {:body {:status 200 :message "Deu certo !!! Higor Here !!!"}})

    (def routes (route/expand-routes #{["/hello" :get funcao-hello :route-name :hello-world]
                                       ["/tarefa" :post criar-tarefa :route-name :criar-tarefa]
                                       ["/tarefa" :get listar-tarefas :route-name :listar-tarefas]
                                       ["/tarefa/:id" :delete remover-tarefa :route-name :remover-tarefas]
                                       ["/tarefa/:id" :patch atualizar-tarefa :route-name :atualizar-tarefas]
                                       ["/teste" :get teste :route-name :teste]
                                       }))

    (assoc this :endpoints routes))

  (stop [this]
    (println "Stop rotas")
    (assoc this :endpoints nil))
  )

(defn new-rotas []
  (->Rotas))


