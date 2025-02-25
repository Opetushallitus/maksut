(ns maksut.oph-url-properties
  (:require [clojure.string :as string]
            [maksut.config :as c]
            [schema.core :as s]
            [clojure.walk :as walk])
  (:import [fi.vm.sade.properties OphProperties]))

(def ^OphProperties url-properties (atom nil))

(s/defn load-config
  [config :- c/MaksutConfig]
  (let [{:keys [virkailija-baseurl
                maksut-url]} (-> config :urls)
        [virkailija-protocol
         virkailija-host] (string/split virkailija-baseurl #":\/\/")
        oph-properties (doto (OphProperties. (into-array String ["/maksut-oph.properties"]))
                         (.addDefault "virkailija.protocol" virkailija-protocol)
                         (.addDefault "host.virkailija" virkailija-host)
                         (.addDefault "url-maksut" maksut-url))]
    (reset! url-properties oph-properties)))

(s/defn resolve-url
  [key :- s/Keyword
   config :- c/MaksutConfig
   & params]
  (when (nil? @url-properties)
    (load-config config))
  (let [params (walk/stringify-keys (or params {}))]
    (.url @url-properties (name key) (to-array params))))
