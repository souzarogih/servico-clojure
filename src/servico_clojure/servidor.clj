(ns servico-clojure.servidor
  (:require [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]
            [io.pedestal.test :as test]
            [servico-clojure.database :as database]))

; {id {tarefa_id, tarefa_nome, tarefa_status}}

(defn assoc-store [context]
  (update context :request assoc :store database/store))

(def db-interceptor
  {:name :db-interceptor
   :enter assoc-store})

(defn lista-tarefas [request]
  {:status 200 :body @(:store request)})

(defn criar-tarefa-mapa [uuid nome status]
  {:id uuid :nome nome :status status})

(defn criar-tarefa [request]
  (let [uuid (java.util.UUID/randomUUID)
        nome (get-in request [:query-params :nome])
        status (get-in request [:query-params :status])
        tarefa (criar-tarefa-mapa uuid nome status)
        store (:store request)]
    (swap! store assoc uuid tarefa)
    {:status 200 :body {:mensagem "Tarefa adicionada com sucesso!"
                        :tarefa tarefa}}))

(defn funcao-hello [request]
  {:status 200 :body (str "Hello World " (get-in request [:query-params :name] "Everybady"))})

(def routes (route/expand-routes #{["/hello" :get funcao-hello :route-name :hello-world]
                                   ["/tarefa" :post  [db-interceptor criar-tarefa] :route-name :criar-tarefa]
                                   ["/tarefa" :get [db-interceptor lista-tarefas] :route-name :lista-tarefas]}))

(def service-map {::http/routes routes
                  ::http/port   9999
                  ::http/type   :jetty
                  ::http/join   false})

(defonce server (atom nil))

(defn start-server []
  (reset! server (http/start (http/create-server service-map))))

(defn test-request [verb url]
  (test/response-for (::http/service-fn @server) verb url))

(defn stop-server []
  (http/stop @server))

(defn restart-server []
  (stop-server)
  (start-server))

(start-server)
;(restart-server)
(println "Server started/restarted")

(clojure.edn/read-string (test-request :get "/tarefa"))

;(println (test-request :get "/hello?name=Cesar"))
;(println (test-request :post "/tarefa?nome=Correr&status=pendente"))
;(println (test-request :post "/tarefa?nome=Correr&status=feito"))

;Lista todas as tarefas
;(println (test-request :get "/tarefa"))

(println @database/store)

;(test-request :get "/hello?name=Cesar")

;(println "Started server http")
