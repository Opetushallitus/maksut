(ns maksut.maksut.maksut-service-spec
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [maksut.maksut.maksut-service-protocol :as maksut-protocol]
            [maksut.maksut.fixtures :as maksut-test-fixtures]
            [maksut.util.date :refer [plus-days-from-now]]
            [maksut.test-fixtures :as test-fixtures :refer [test-system]])
  (:import (java.sql Date)))

(use-fixtures :once test-fixtures/with-mock-system)
(use-fixtures :each test-fixtures/with-empty-database)

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
        due-date (plus-days-from-now 7)
        date (str due-date)]

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
                             :due_date (str (plus-days-from-now 14))
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
                      :due_date (str (plus-days-from-now 14))
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
                      :due_date (str (plus-days-from-now 7))
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
             (let [new-date (str (.plusDays due-date 30))
                   lasku (merge (select-keys hannes [:first-name :email])
                                {:application-key application-key
                                 :last-name "Atria"
                                 :amount "555.12"
                                 :due-date new-date
                                 :index index})
                   response  (maksut-protocol/create-tutu service maksut-test-fixtures/fake-session lasku)]
               (is (= date (:due_date response)))))

    (testing "Try to change due-date of existing ASTU invoice with extend-deadline unsuccessfully"
      (let [old-date (str (plus-days-from-now 14))
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
      (let [new-date (str (plus-days-from-now 30))
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
      (let [existing-date (str (plus-days-from-now 30))
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
                 (is (->> list (map :order_id) sort) '(order-id order-id-2)))))))

(deftest maksut-eraaantynyt-korjaus-test
  (let [service     (:maksut-service @test-system)
        db          (:db @test-system)
        hannes      {:first-name "Hannes" :last-name "Snellmann" :email "hannes@gmail.com"}
        ak-active   "1.2.246.562.11.00000000000000200001"
        ak-overdue  "1.2.246.562.11.00000000000000200002"
        ak-delete   "1.2.246.562.11.00000000000000200003"
        ak-due-date "1.2.246.562.11.00000000000000200004"
        future-date (date->iso (time/from-now (time/days 7)))
        new-date    (date->iso (time/from-now (time/days 30)))]

    (testing "force-invalidate-laskut invalidoi aktiivisen laskun"
      (maksut-protocol/create service maksut-test-fixtures/fake-session
        (merge hannes {:reference ak-active
                       :origin    "kkhakemusmaksu"
                       :amount    "100.00"
                       :due-days  7}))
      (let [result (maksut-protocol/force-invalidate-laskut service maksut-test-fixtures/fake-session
                     {:keys [ak-active]})]
        (is (= 1 (count result)))
        (is (= :invalidated (:status (first result))))))

    (testing "force-invalidate-laskut invalidoi erääntyneen laskun (ohittaa päivämääräehdon)"
      (test-fixtures/add-invoice! db
        {:order_id   "KKHA200002"
         :first_name "Hannes"
         :last_name  "Snellmann"
         :email      "hannes@gmail.com"
         :amount     100.00M
         :origin     "kkhakemusmaksu"
         :reference  ak-overdue
         :due_date   (Date/valueOf "2020-01-01")})
      (let [result (maksut-protocol/force-invalidate-laskut service maksut-test-fixtures/fake-session
                     {:keys [ak-overdue]})]
        (is (= 1 (count result)))
        (is (= :invalidated (:status (first result))))))

    (testing "delete-laskut poistaa laskun ja sen salaisuuden"
      (maksut-protocol/create service maksut-test-fixtures/fake-session
        (merge hannes {:reference ak-delete
                       :origin    "kkhakemusmaksu"
                       :amount    "100.00"
                       :due-days  7}))
      (is (= 1 (count (maksut-protocol/list-laskut service maksut-test-fixtures/fake-session
                        {:application-key ak-delete}))))
      (let [result (maksut-protocol/delete-laskut service maksut-test-fixtures/fake-session
                     {:keys [ak-delete]})]
        (is (= {:deleted 1} result)))
      (is (= 0 (count (maksut-protocol/list-laskut service maksut-test-fixtures/fake-session
                        {:application-key ak-delete})))))

    (testing "update-laskut-due-date päivittää eräpäivän ja lasku pysyy aktiivisena"
      (maksut-protocol/create service maksut-test-fixtures/fake-session
        (merge hannes {:reference ak-due-date
                       :origin    "kkhakemusmaksu"
                       :amount    "100.00"
                       :due-days  7}))
      (let [statuses (maksut-protocol/update-laskut-due-date service maksut-test-fixtures/fake-session
                       {:keys [ak-due-date] :due-date new-date})]
        (is (= 1 (count statuses)))
        (is (= :active (:status (first statuses)))))
      (let [lasku (first (maksut-protocol/list-laskut service maksut-test-fixtures/fake-session
                           {:application-key ak-due-date}))]
        (is (= new-date (:due_date lasku)))))))
