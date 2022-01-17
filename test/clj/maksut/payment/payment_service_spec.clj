(ns maksut.payment.payment-service-spec
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [maksut.payment.payment-service-protocol :as payment-protocol]
            [maksut.maksut.maksut-service-protocol :as maksut-protocol]
            [clj-time.core :as time]
            [clj-time.format :as format]
            [clj-time.coerce :refer [to-sql-date]]
            [clojure.java.jdbc :as jdbc]
            [maksut.maksut.fixtures :as maksut-test-fixtures]
            [maksut.test-fixtures :as test-fixtures :refer [dispatch-mock
                                                            test-system
                                                            add-invoice!
                                                            get-emails
                                                            is-email-count
                                                            reset-emails!]]
            [maksut.api-schemas :as api-schemas]))


(use-fixtures :once test-fixtures/with-mock-system)
(use-fixtures :each test-fixtures/with-empty-database)

(defn date->iso [date]
  (format/unparse (format/formatters :date) date))

(defmacro catch-thrown-info [f]
  `(try
    ~f
    (catch
      clojure.lang.ExceptionInfo e#
      {:msg (ex-message e#) :data (ex-data e#)})))

(defn db-invoice [date]
  {:order_id "TTU123456-1"
   :first_name "Hannes"
   :last_name "Snellmann"
   :email "hannes@gmail.com"
   :amount (bigdec "123.00")
   :origin "tutu"
   :reference "1.2.246.562.11.00000000000000123456"
   :due_date (to-sql-date date)})

(defn db-invoice-2 [date]
  {:order_id "TTU123456-2"
   :first_name "Hannes"
   :last_name "Snellmann"
   :email "hannes@gmail.com"
   :amount (bigdec "5000.12")
   :origin "tutu"
   :reference "1.2.246.562.11.00000000000000123456"
   :due_date (to-sql-date date)})

(def params {
  :ORDER_NUMBER "TTU123456-1"
  :PAYMENT_ID "106173855345"
  :AMOUNT "70.00"
  :TIMESTAMP 1642348919
  :STATUS "PAID"
  :RETURN_AUTHCODE "937C2B1AD54A3460A54335137F55BE1CEC2374481214A9CDB013E5598EDB44DD" ;based on secret "sikrot"
  })

(def params-2 {
  :ORDER_NUMBER "TTU123456-2"
  :PAYMENT_ID "106173855345"
  :AMOUNT "5000.12"
  :TIMESTAMP 1642348919
  :STATUS "PAID"
  :RETURN_AUTHCODE "4DA539ADFDCAD3E9C4FE1F05974C2E165DC190CF74CF0B681D7DBA4A95CD7E4A" ;based on secret "sikrot"
  })


(deftest tutu-maksut-complete-happy-flow
  (let [service (:payment-service @test-system)
        db      (:db @test-system)
        due-date (time/from-now (time/days 14))
        date    (date->iso due-date)
        db-data (db-invoice due-date)
        locale  "fi"
        secret  "foobar"
        invoice-insert (test-fixtures/add-invoice! db db-data)
        invoice-id (-> invoice-insert first :id)]

    (reset-emails!)
    (jdbc/insert! db :secrets {:fk_invoice invoice-id
                               :secret secret})

    (testing "Get Paytrail form-data"
             (let [{:keys [uri params]} (payment-protocol/tutu-payment service
                                                                       maksut-test-fixtures/fake-session
                                                                       {:order-id (:order_id db-data)
                                                                        :locale locale
                                                                        :secret secret})]
               (is (= (:LOCALE params) "fi_FI"))
               (is (= (:ORDER_NUMBER params) (:order_id db-data)))
               (is (= (:AUTHCODE params) "1FEDE4BB0D956DCA9D4FF19076E026FF6E202D6640B8056AF9D2C62225CE6B76"))
               ))

    (testing "Try to pay invoice with invalid secret"
             (let [exc (catch-thrown-info (payment-protocol/tutu-payment service maksut-test-fixtures/fake-session
                                                                         {:order-id (:order_id db-data)
                                                                          :locale locale
                                                                          :secret "eioikeasecret"}))
                   data (:data exc)]
               (is (= (:type data) :maksut.error))
               (is (= (:code data) :invoice-notfound))
               ))

    (testing "Payment success callback"
        (let [response  (payment-protocol/process-success-callback service params locale false)
              emails-to-user (filter #(= (-> % :recipients first) (:email db-data)) (get-emails))
              first-subject (-> emails-to-user first :subject)]
          (is (= (:action response) :created))
          (is-email-count 1)
          (is (= (count emails-to-user) 1))
          (is (.contains first-subject "Käsittelymaksusi on vastaanotettu"))
          (reset-emails!)
          )
        )

    (testing "Try to pay invoice after it has been paid"
             (let [exc (catch-thrown-info (payment-protocol/tutu-payment service maksut-test-fixtures/fake-session
                                                                         {:order-id (:order_id db-data)
                                                                          :locale locale
                                                                          :secret secret}))
                   data (:data exc)]
               (is (= (:type data) :maksut.error))
               (is (= (:code data) :invoice-invalidstate-paid))
               ))

    (testing "Payment with invalid AUTOCODE"
             (let [inv-params (assoc params
                                     :RETURN_AUTHCODE
                                     "5DA539ADFDCAD3E9C4FE1F05974C2E165DC190CF74CF0B681D7DBA4A95CD7E4B")
                   response  (payment-protocol/process-success-callback service inv-params locale false)]
               (is (= (:action response) :error))
               (is (= (:code response) :payment-invalid-status))
               (is-email-count 0)
               ))

    (testing "Payment with invalid STATUS"
             (let [inv-params (-> params
                                  (assoc :STATUS "CANCELLED")
                                  (assoc :RETURN_AUTHCODE "62549431C803ADBBF4DE66BDCF927833AB5848DBAC686F37EF011807A789B2B0"))
                   response  (payment-protocol/process-success-callback service inv-params locale false)]
               (is (= (:action response) :error))
               (is (= (:code response) :payment-invalid-status))
               (is-email-count 0)
               ))

    (testing "Payment success callback AGAIN" ;invoice from previous test is still in database, but now it's paid
             (let [response  (payment-protocol/process-success-callback service params locale false)]
               (is (= (:action response) :not-modified))
               (is-email-count 0)
               ))

    (testing "Payment 1. notify callback"
             (let [response  (payment-protocol/process-success-callback service params locale true)]
               (is (= (:action response) :not-modified))
               (is-email-count 0)
               ))

    (testing "Payment 2. notify callback"
             (let [response  (payment-protocol/process-success-callback service params locale true)]
               (is (= (:action response) :not-modified))
               (is-email-count 0)
               ))

    (testing "2nd payment success callback"
             (test-fixtures/add-invoice! db (db-invoice-2 due-date))

             (let [response       (payment-protocol/process-success-callback service params-2 locale false)
                   emails-to-user (filter #(= (-> % :recipients first) (:email db-data)) (get-emails))
                   first-subject  (-> emails-to-user first :subject)]
               (is (= (:action response) :created))
               (is-email-count 1)
               (is (= (count emails-to-user) 1))
               (is (.contains first-subject "Päätösmaksusi on vastaanotettu"))
               (reset-emails!))
             )

  )
)

;Rare use-case where multiple Paytrail sessions are initiated before payments have been finalized,
;and then more than one is finalized will results in multiple rows in payments table (with unique PAYMENT_ID)
(deftest maksut-double-payment
  (let [service (:payment-service @test-system)
        db      (:db @test-system)

        due-date (time/from-now (time/days 14))
        date (date->iso due-date)
        locale  "fi"]

    (testing "First payment is ok"
             (test-fixtures/add-invoice! db (db-invoice due-date))

             (let [response  (payment-protocol/process-success-callback service params locale false)]
               (is (= (:action response) :created))))


    (testing "Second payment is ok"
             (let [diff-params (-> params
                                   (assoc :PAYMENT_ID "123456")
                                   (assoc :AMOUNT "100.00")
                                   (assoc :RETURN_AUTHCODE "41CB4448D9A47BB7AC299917E881E3D441728B45FE03A2FC2FEBCDECD621B162"))
                    response  (payment-protocol/process-success-callback service diff-params locale false)
                    total     (->
                               (jdbc/query db ["SELECT SUM(amount) AS total FROM payments WHERE fk_invoice = (SELECT id FROM invoices WHERE reference = '1.2.246.562.11.00000000000000123456' LIMIT 1)"])
                               first
                               :total)]
               (is (= (:action response) :created))
               (is (= total (bigdec 170)))))


  )
)

(deftest english-emails
  (let [service (:payment-service @test-system)
       db      (:db @test-system)

       due-date (time/from-now (time/days 14))
       date (date->iso due-date)
       db-data (db-invoice due-date)
       locale  "en"]

    (reset-emails!)

    (testing "Confirmation email is in English"
            (test-fixtures/add-invoice! db db-data)

            (let [response  (payment-protocol/process-success-callback service params locale false)
                  emails-to-user (filter #(= (-> % :recipients first) (:email db-data)) (get-emails))
                  first-subject (-> emails-to-user first :subject)]
              (is (= (:action response) :created))
              (is-email-count 1)
              (is (= (count emails-to-user) 1))
              (is (.contains first-subject "Your processing fee has been received"))
              (reset-emails!)
              )
            )
   )
)


(deftest pay-non-existing-invoice
  (let [service (:payment-service @test-system)]
    (testing "Try to pay invoice after it has been paid"
             (let [exc (catch-thrown-info (payment-protocol/tutu-payment service maksut-test-fixtures/fake-session
                                                                         {:order-id "TTU123456-1"
                                                                          :locale "en"
                                                                          :secret "foobar"}))
                   data (:data exc)]
               (is (= (:type data) :maksut.error))
               (is (= (:code data) :invoice-notfound))
               ))))


(deftest pay-overdue-invoice
  (let [service (:payment-service @test-system)
        maksut-service (:maksut-service @test-system)
        db      (:db @test-system)

        due-date (time/from-now (time/days -30))
        date (date->iso due-date)
        db-data (db-invoice due-date)
        secret  "foobar"
        invoice-insert (test-fixtures/add-invoice! db db-data)
        invoice-id (-> invoice-insert first :id)]

    (jdbc/insert! db :secrets {:fk_invoice invoice-id
                               :secret secret})

    (testing "Try to pay invoice after due-date"
             (let [exc (catch-thrown-info (payment-protocol/tutu-payment service maksut-test-fixtures/fake-session
                                                                         {:order-id (:order_id db-data)
                                                                          :locale "fi"
                                                                          :secret secret}))
                   data (:data exc)]
               (is (= (:type data) :maksut.error))
               (is (= (:code data) :invoice-invalidstate-overdue))
               ))

    (testing "Try to edit overdue invoice"
             (let [lasku {:application-key (:reference db-data)
                          :first-name (:first_name db-data)
                          :last-name (:last_name db-data)
                          :email (:email db-data)
                          :amount "222.00"
                          :index 1}]
               (let [exc (catch-thrown-info (maksut-protocol/create-tutu maksut-service maksut-test-fixtures/fake-session lasku))
                     data (:data exc)]
                 (is (= (:type data) :maksut.error))
                 (is (= (:code data) :invoice-invalidstate-overdue)))))
    ))

(deftest pay-at-due-date
  (let [service (:payment-service @test-system)
        db      (:db @test-system)

        due-date (time/from-now (time/days 0))
        date (date->iso due-date)
        db-data (db-invoice due-date)
        secret  "foobar"
        invoice-insert (test-fixtures/add-invoice! db db-data)
        invoice-id (-> invoice-insert first :id)]

    (jdbc/insert! db :secrets {:fk_invoice invoice-id
                               :secret secret})

    (testing "Pay invoice at due-date"
             (let [{:keys [params]} (payment-protocol/tutu-payment service maksut-test-fixtures/fake-session
                                                           {:order-id (:order_id db-data)
                                                            :locale "fi"
                                                            :secret secret})]
               (is (= (:ORDER_NUMBER params) (:order_id db-data)))
               ))
    ))

(deftest try-to-change-invoice-after-paying
         (let [service (:payment-service @test-system)
               maksut-service (:maksut-service @test-system)
               db      (:db @test-system)
               due-date (time/from-now (time/days 14))
               date    (date->iso due-date)
               db-data (db-invoice due-date)
               locale  "fi"
               secret  "foobar"
               invoice-insert (test-fixtures/add-invoice! db db-data)
               invoice-id (-> invoice-insert first :id)]

           (jdbc/insert! db :secrets {:fk_invoice invoice-id
                                      :secret secret})

           (testing "Pay successfully"
                    (let [response (payment-protocol/process-success-callback service params locale false)]
                      (is (= (:action response) :created))
                      )
                    )

           (testing "Try to edit invoice"
                    (let [lasku {:application-key (:reference db-data)
                                 :first-name (:first_name db-data)
                                 :last-name (:last_name db-data)
                                 :email (:email db-data)
                                 :amount "222.00"
                                 :index 1}]
                      (let [exc (catch-thrown-info (maksut-protocol/create-tutu maksut-service maksut-test-fixtures/fake-session lasku))
                            data (:data exc)]
                        (is (= (:type data) :maksut.error))
                        (is (= (:code data) :invoice-invalidstate-paid)))))

           ))
