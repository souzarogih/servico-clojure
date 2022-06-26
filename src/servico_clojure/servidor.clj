(ns servico-clojure.servidor
  (:require [io.pedestal.http :as http]
            [servico-clojure.database :as database]
            [io.pedestal.interceptor :as i]
            [io.pedestal.test :as test]
            [com.stuartsierra.component :as component]))



(defrecord Servidor [database rotas]
  component/Lifecycle

  (start [this]

    (println "Starting Server in port 9999")

    (defn assoc-store [context]
      (update context :request assoc :store (:store database)))

    (def db-interceptor
      {:name  :db-interceptor
       :enter assoc-store})

    (def service-map-base {::http/routes (:endpoints rotas)
                           ::http/port   9999
                           ::http/type   :jetty
                           ::http/join   false})

    (def service-map (-> service-map-base
                         (http/default-interceptors)
                         (update ::http/interceptors conj (i/interceptor db-interceptor))))

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

    ;(restart-server)
    ;(start-server)
    ;(start-now)
    ;(println "Server started/restarted")

    (defn start []
      (try
        (start-server)
        (catch Exception e (println "Erro ao executar start" (.getMessage e))))

      (try
        (restart-server)
        (catch Exception e (println "Erro ao executar restart" (.getMessage e)))))

    (start)

    (assoc this :test-request test-request))

  (stop [this]
    (assoc this :test-request nil))

  )

(defn new-servidor []
  (map->Servidor {}))

;(println @database/store)
;(println "Started server http")
