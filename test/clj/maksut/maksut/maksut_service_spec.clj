(ns maksut.maksut.maksut-service-spec
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [maksut.maksut.maksut-service-protocol :as maksut-protocol]
            [clj-time.core :as time]
            [clj-time.format :as format]
            [maksut.maksut.fixtures :as maksut-test-fixtures]
            [maksut.test-fixtures :as test-fixtures :refer [test-system]]
            [maksut.api-schemas :as api-schemas]))


(use-fixtures :once test-fixtures/with-mock-system)
(use-fixtures :each test-fixtures/with-empty-database)

(defn date->iso [date]
  (format/unparse (format/formatters :date) date))

(def hannes {:first-name "Hannes"
             :last-name "Snellmann"
             :email "hannes@gmail.com"})

(deftest maksut-create-test
  (let [service (:maksut-service @test-system)
        application-key "1.2.246.562.11.00000000000000123456"
        order-id "TTU123456-1"
        order-id-2 "TTU123456-2"
        first-secret (atom nil)
        amount "123"
        index 1
        due-date (time/from-now (time/days 7))
        date (date->iso due-date)]

    (testing "Create invoice"
      (let [lasku (merge (select-keys hannes [:first-name :last-name :email])
                               {:application-key application-key
                                :amount amount
                                :due-date date
                                :index index})
            expected {:order_id order-id
                      :first_name (:first-name hannes)
                      :last_name (:last-name hannes)
                      :amount "123.00"
                      :due_date date
                      :status :active
                      :paid_at ""
                      :reference application-key
                      :origin "tutu"}]
        (let [response  (maksut-protocol/create-tutu service maksut-test-fixtures/fake-session lasku)
              secret    (:secret response)
              wo-secret (dissoc response :secret)]
          (reset! first-secret secret)
          (is (string? secret))
          (is (> (count secret) 0))
          (is (= wo-secret expected)))))

    (testing "Create 2. invoice"
             (let [lasku (merge (select-keys hannes [:first-name :last-name :email])
                                {:reference application-key
                                 :origin "tutu"
                                 :amount "1000"
                                 :due-days 14
                                 :index 2})
                   expected {:order_id order-id-2
                             :first_name (:first-name hannes)
                             :last_name (:last-name hannes)
                             :amount "1000.00"
                             :due_date (date->iso (time/from-now (time/days 14)))
                             :status :active
                             :paid_at ""
                             :reference application-key
                             :origin "tutu"}]
               (let [response  (maksut-protocol/create service maksut-test-fixtures/fake-session lasku)
                     secret    (:secret response)
                     wo-secret (dissoc response :secret)]
                 (is (string? secret))
                 (is (> (count secret) 0))
                 (is (= wo-secret expected)))))

    (testing "Edit previously created invoice"
           (let [lasku (merge (select-keys hannes [:first-name :email])
                              {:application-key application-key
                               :last-name "Atria"
                               :amount "555.12"
                               :due-date date
                               :index index})
                 expected {:order_id order-id
                           :first_name (:first-name hannes)
                           :last_name "Atria"
                           :amount "555.12"
                           :due_date date
                           :status :active
                           :paid_at ""
                           :reference application-key
                           :origin "tutu"}]
             (let [response  (maksut-protocol/create-tutu service maksut-test-fixtures/fake-session lasku)
                   secret    (:secret response)
                   wo-secret (dissoc response :secret)]
               (is (string? secret))
               (is (> (count secret) 0))
               (is (= wo-secret expected)))))

    (testing "Try to change due-date of existing invoice"
             (let [new-date (date->iso (time/plus due-date (time/days 30)))
                   lasku (merge (select-keys hannes [:first-name :email])
                                {:application-key application-key
                                 :last-name "Atria"
                                 :amount "555.12"
                                 :due-date new-date
                                 :index index})
                   expected {:order_id order-id
                             :first_name (:first-name hannes)
                             :last_name "Atria"
                             :amount "555.12"
                             :due_date new-date
                             :status :active
                             :paid_at ""}]
               (let [response  (maksut-protocol/create-tutu service maksut-test-fixtures/fake-session lasku)]
                 (is (= date (:due_date response))))))

    (testing "List created 2 active invoices"
             (let [input {:application-key application-key}]
               (let [list (maksut-protocol/list-laskut service maksut-test-fixtures/fake-session input)]
                 (is (= (count list) 2))
                 (is (map :status list) '(:active :active)))))

    (testing "Mass-check statuses"
             (let [input {:keys [application-key]}]
               (let [list (maksut-protocol/check-status service maksut-test-fixtures/fake-session input)]
                 (is (= (count list) 2))
                 (is (->> list (map :order-id) sort first) order-id)
                 (is (map :status list) '(:active :active)))))

    (testing "Get laskut by secret"
             (let [secret @first-secret]
               (let [list (maksut-protocol/get-laskut-by-secret service maksut-test-fixtures/fake-session secret)]
                 (is (->> list (map :order_id) sort) '(order-id order-id-2)))))

  )
)
