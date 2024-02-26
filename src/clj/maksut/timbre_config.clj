(ns maksut.timbre-config
  (:require [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.core :refer [println-appender]]
            [taoensso.timbre.appenders.community.rolling :refer [rolling-appender]]
            [timbre-ns-pattern-level]
            [environ.core :refer [env]])
  (:import [java.util TimeZone]))

(defn configure-logging! [config]
  (timbre/merge-config!
    {:appenders
                     {:standard-out     {:enabled? false}
                      :println          (println-appender {:stream :std-out})
                      :rolling-appender (rolling-appender
                                          {:path    (str (-> config :log :base-path)
                                                         "/app_maksut"
                                                         (when (:hostname env) (str "_" (:hostname env))))
                                           :pattern :daily})}
     :middleware     [(timbre-ns-pattern-level/middleware {"com.zaxxer.hikari.HikariConfig" :debug
                                                           :all                             :info})]
     :timestamp-opts {:pattern  "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
                      :timezone (TimeZone/getTimeZone "Europe/Helsinki")}
     :output-fn      (partial timbre/default-output-fn {:stacktrace-fonts {}})}))
