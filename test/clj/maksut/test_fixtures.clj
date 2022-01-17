(ns maksut.test-fixtures
  (:require [maksut.cas.mock.mock-dispatcher-protocol :as mock-dispatcher-protocol]
            [maksut.config :as c]
            [com.stuartsierra.component :as component]
            [maksut.system :as system]
            [clojure.test :refer [is]]
            [clojure.java.jdbc :as jdbc]))


(def test-system (atom nil))

(defn- start-system [& _args]
  (let [config (c/make-config)
        sys (-> (system/maksut-system config)
                (dissoc :auth-routes-source
                        :health-checker
                        :http-server
                        ))]
    (reset! test-system (component/start-system sys))))

(defn- stop-system []
  (when @test-system
    (component/stop-system @test-system)
    (reset! test-system nil)))

(defn dispatch-mock [spec]
  (let [mock-dispatcher (:mock-dispatcher @test-system)]
    (mock-dispatcher-protocol/dispatch-mock mock-dispatcher spec)))

(defn get-emails []
  @(:mock-email-service-list @test-system))

(defn is-email-count [amount]
  (is (= (count (get-emails)) amount)))

(defn reset-emails! []
  (reset! (:mock-email-service-list @test-system) '()))

(defn with-mock-system [f]
  (start-system)
  (f)
  (stop-system))

(defn add-invoice! [db columns]
  (jdbc/insert! db :invoices columns))

(defn- truncate-database [db]
  (jdbc/execute! db ["TRUNCATE invoices CASCADE"])
  )

(defn with-empty-database [f]
  (truncate-database (:db @test-system))
  (f)
  (truncate-database (:db @test-system)))

