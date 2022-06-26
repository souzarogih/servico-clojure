(ns servico-clojure.api-test
  (:require [clojure.test :refer :all]
            )
  (:use [clojure.pprint]))


(pprint component-result)

(deftest tarefa-api-test
  (testing "Hello world test")
  (let [path "/hello"
        response (test-request :get path "?name=Cesar")
        body (:body response)]
    (is (= "Hello World Cesar" body))
    )
  )

;(defn test-request [verb url]
;  (test/response-for (::http/service-fn @server) verb url))
;(clojure.edn/read-string (test-request :get "/tarefa"))

;(test-request :get "/hello?name=Cesar")
;(println (test-request :get "/hello?name=Cesar"))
;(println (test-request :post "/tarefa?nome=Correr&status=pendente"))
;(println (test-request :post "/tarefa?nome=Correr&status=feito"))
;(println (test-request :delete "/tarefa/8054f958-b10a-4c1f-9f4f-d66d25017a10"))
;(println (test-request :patch "/tarefa/8054f958-b10a-4c1f-9f4f-d66d25017a10?nome=Leitura&status=Pendente"))
;(println (test-request :get "/hello?name=Cesar"))
;(println (test-request :get "/tarefa"))