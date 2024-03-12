(ns maksut.config
  (:require [clojure.edn :as edn]
            [config.core :as c]
            [maksut.public-config-schemas :as public]
            [schema.core :as s]))

(s/defschema MaksutConfig
  {:server               s/Any                              ; this goes straight to jetty where keys have defaults
   :log                  {:base-path s/Str}
   :db                   {:username      s/Str
                          :password      s/Str
                          :database-name s/Str
                          :host          s/Str
                          :port          s/Int}
   :payment              {:paytrail-config {:host             s/Str
                                            :merchant-id      s/Int
                                            :merchant-secret  s/Str}
                          :callback-uri       s/Str
                          :order-id-prefix    s/Str
                          :currency           s/Str
                          }
   :tutu                 {:lasku-origin       s/Str
                          :order-id-prefix    s/Str}
   :file-store           {:engine s/Keyword
                          :filesystem {:base-path s/Str}
                          :s3 {:bucket s/Str
                               :region s/Str}
                          :attachment-mime-types [s/Str]}
   :cas                  {:username s/Str
                          :password s/Str
                          :services {:kayttooikeus          {:service-url-property s/Keyword
                                                             :session-cookie-name  s/Str}
                                     :email                 {:service-url-property s/Keyword
                                                             :session-cookie-name  s/Str}}}
   :urls                 {:virkailija-baseurl        s/Str
                          :maksut-url s/Str
                          :oppija-baseurl s/Str}
   :oph-organisaatio-oid s/Str
   :public-config        public/PublicConfig})

(defn- report-error [^Throwable e message]
  (.println System/err message)
  (.printStackTrace e)
  (throw e))

(defn- parse-edn [source-string]
  (try
    (edn/read-string source-string)
    (catch Exception e
      (report-error e "Ei saatu jäsennettyä EDN:ää syötteestä."))))

(defn- validate-config [config-edn]
  (let [validation-result (s/check MaksutConfig config-edn)]
    (if validation-result
      (let [message (str "Rikkinäinen konfiguraatio: " validation-result)]
        (report-error (IllegalArgumentException.) message))
      config-edn)))

(s/defn make-config :- MaksutConfig []
  (-> (:config c/env)
      (#(do (println (str "Luetaan konfiguraatio tiedostosta '" % "'")) %))
      (slurp)
      (parse-edn)
      (validate-config)))

(s/defn production-environment? [config :- MaksutConfig]
  (= :production (get-in config [:public-config :environment])))

(s/defn integration-environment? [config :- MaksutConfig]
  (= :it (get-in config [:public-config :environment])))

(s/defn development-environment? [config :- MaksutConfig]
  (= :development (get-in config [:public-config :environment])))
