(ns maksut.maksut.maksut-service-spec
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [maksut.maksut.maksut-service-protocol :as maksut-protocol]
            [clj-time.core :as time]
            [clj-time.format :as format]
            [maksut.maksut.fixtures :as maksut-test-fixtures]
            [maksut.test-fixtures :as test-fixtures :refer [test-system]]))


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
        application-key2 "1.2.246.562.11.00000000000000654321"
        application-key3 "1.2.246.562.11.00000000000000111111"
        order-id "TTU123456-1"
        order-id-2 "TTU123456-2"
        order-id-3 "AKR654321-2"
        order-id-4 "KKHA111111"
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

    (testing "Create ASTU invoice"
      (let [lasku (merge (select-keys hannes [:first-name :last-name :email])
                         {:reference application-key2
                          :origin "astu"
                          :amount "1000"
                          :due-days 14
                          :index 2
                          :metadata {:form-name {:fi "ASTU FI"
                                                 :sv "ASTU SV"
                                                 :en "ASTU EN"}
                                     :order-id-prefix "AKR"}})
            expected {:order_id order-id-3
                      :first_name (:first-name hannes)
                      :last_name (:last-name hannes)
                      :amount "1000.00"
                      :due_date (date->iso (time/from-now (time/days 14)))
                      :status :active
                      :paid_at ""
                      :reference application-key2
                      :origin "astu"
                      :metadata {:form_name {:fi "ASTU FI"
                                             :sv "ASTU SV"
                                             :en "ASTU EN"}
                                 :order_id_prefix "AKR"}}]
        (let [response  (maksut-protocol/create service maksut-test-fixtures/fake-session lasku)
              secret    (:secret response)
              wo-secret (dissoc response :secret)]
          (is (string? secret))
          (is (> (count secret) 0))
          (is (= wo-secret expected)))))

    (testing "Create kk-application-payment invoice"
      (let [lasku (merge (select-keys hannes [:first-name :last-name :email])
                         {:reference application-key3
                          :origin "kkhakemusmaksu"
                          :amount "100.00"
                          :due-days 7
                          :metadata {:haku-name {:fi "Haku FI"
                                                 :sv "Haku SV"
                                                 :en "Haku EN"}
                                     :alkamiskausi "kausi_s"
                                     :alkamisvuosi 2025}})
            expected {:order_id order-id-4
                      :first_name (:first-name hannes)
                      :last_name (:last-name hannes)
                      :amount "100.00"
                      :due_date (date->iso (time/from-now (time/days 7)))
                      :status :active
                      :paid_at ""
                      :reference application-key3
                      :origin "kkhakemusmaksu"
                      :metadata {:haku_name {:fi "Haku FI"
                                             :sv "Haku SV"
                                             :en "Haku EN"}
                                 :alkamiskausi "kausi_s"
                                 :alkamisvuosi 2025}}]
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

    (testing "Try to change due-date of existing TUTU invoice unsuccessfully"
             (let [new-date (date->iso (time/plus due-date (time/days 30)))
                   lasku (merge (select-keys hannes [:first-name :email])
                                {:application-key application-key
                                 :last-name "Atria"
                                 :amount "555.12"
                                 :due-date new-date
                                 :index index})
                   response  (maksut-protocol/create-tutu service maksut-test-fixtures/fake-session lasku)]
               (is (= date (:due_date response)))))

    (testing "Try to change due-date of existing ASTU invoice with extend-deadline unsuccessfully"
      (let [old-date (date->iso (time/from-now (time/days 14)))
            lasku (merge (select-keys hannes [:first-name :last-name :email])
                         {:reference application-key2
                          :origin "astu"
                          :amount "1000"
                          :extend-deadline true
                          :due-days 31
                          :index 2
                          :metadata {:form-name {:fi "ASTU FI"
                                                 :sv "ASTU SV"
                                                 :en "ASTU EN"}
                                     :order-id-prefix "AKR"}})
            response  (maksut-protocol/create service maksut-test-fixtures/fake-session lasku)]
        (is (= old-date (:due_date response)))))

    (testing "Change due-date of existing kk-application-payment invoice with extend-deadline successfully"
      (let [new-date (date->iso (time/from-now (time/days 30)))
            lasku (merge (select-keys hannes [:first-name :last-name :email])
                         {:reference application-key3
                          :origin "kkhakemusmaksu"
                          :amount "100.00"
                          :due-days 30
                          :extend-deadline true
                          :metadata {:haku-name {:fi "Haku FI"
                                                 :sv "Haku SV"
                                                 :en "Haku EN"}
                                     :alkamiskausi "kausi_s"
                                     :alkamisvuosi 2025}})
            response  (maksut-protocol/create service maksut-test-fixtures/fake-session lasku)]
        (is (= new-date (:due_date response)))))

    (testing "Attempt to change due-date of existing kk-application-payment invoice without extend-deadline unsuccessfully"
      (let [existing-date (date->iso (time/from-now (time/days 30)))
            lasku (merge (select-keys hannes [:first-name :last-name :email])
                         {:reference application-key3
                          :origin "kkhakemusmaksu"
                          :amount "100.00"
                          :due-days 45
                          :metadata {:haku-name {:fi "Haku FI"
                                                 :sv "Haku SV"
                                                 :en "Haku EN"}
                                     :alkamiskausi "kausi_s"
                                     :alkamisvuosi 2025}})
            response  (maksut-protocol/create service maksut-test-fixtures/fake-session lasku)]
        (is (= existing-date (:due_date response)))))

    (testing "List created 2 active invoices"
             (let [input {:application-key application-key}]
               (let [list (maksut-protocol/list-laskut service maksut-test-fixtures/fake-session input)]
                 (is (= (count list) 2))
                 (is (map :status list) '(:active :active)))))

    (testing "Mass-check statuses"
             (let [input {:keys [application-key]}]
               (let [list (maksut-protocol/check-status service maksut-test-fixtures/fake-session input)]
                 (is (= (count list) 2))
                 (is (->> list (map :order_id) sort first) order-id)
                 (is (map :status list) '(:active :active)))))

    (testing "Get laskut by secret"
             (let [secret @first-secret]
               (let [list (maksut-protocol/get-laskut-by-secret service maksut-test-fixtures/fake-session secret)]
                 (is (->> list (map :order_id) sort) '(order-id order-id-2)))))

  )
)
