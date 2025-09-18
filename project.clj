(defproject maksut "0.1.0-SNAPSHOT"
  ;TODO Tässä on todennäköisesti paljon kopioitua depsejä mitä ei tarvita, käy läpi myöhemmin mitkä voi jättää pois
  :managed-dependencies [[clj-commons/clj-yaml "1.0.29"]
                         [com.google.protobuf/protobuf-java "3.25.5"]
                         [commons-fileupload "1.6.0"]
                         [commons-io "2.14.0"]
                         [org.apache.commons/commons-compress "1.21"]
                         [org.eclipse.jetty/jetty-server "9.4.57.v20241219"]
                         [org.jsoup/jsoup "1.14.2"]]
  :dependencies [[org.clojure/clojure "1.11.4"]
                 [camel-snake-kebab "0.4.3"]
                 [cheshire "5.12.0"]
                 [clj-http "3.12.3"]
                 [clj-time "0.15.2"]
                 [com.amazonaws/aws-java-sdk-s3 "1.12.663"]
                 [com.taoensso/timbre "6.3.1"]
                 [com.fzakaria/slf4j-timbre "0.4.1"]
                 [timbre-ns-pattern-level "0.1.2"]
                 [com.stuartsierra/component "1.1.0"]
                 [clj-soup/clojure-soup "0.1.3"]
                 [org.flywaydb/flyway-core "10.8.1"]
                 [org.flywaydb/flyway-database-postgresql "10.8.1"]
                 [fi.vm.sade/auditlogger "9.0.0-SNAPSHOT"]
                 [opiskelijavalinnat-utils/java-cas "1.2.4-SNAPSHOT"]
                 [fi.vm.sade.java-utils/java-properties "0.1.0-SNAPSHOT"]
                 [oph/clj-access-logging "1.0.0-SNAPSHOT"]
                 [oph/clj-stdout-access-logging "1.0.0-SNAPSHOT"]
                 [oph/clj-timbre-access-logging "1.1.0-SNAPSHOT"]
                 [oph/clj-timbre-auditlog "0.2.0-SNAPSHOT"]
                 [oph/clj-ring-db-cas-session "0.3.0-SNAPSHOT" :exclusions [javax.mail/mailapi]]
                 [hikari-cp "3.0.1"]
                 [metosin/reitit "0.6.0"]
                 [metosin/schema-tools "0.13.1"]
                 [metosin/ring-swagger-ui "5.9.0"]
                 [org.clojure/core.async "1.6.681"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.clojure/core.match "1.1.0"]
                 [org.postgresql/postgresql "42.7.2"]
                 [com.layerware/hugsql "0.5.3"]
                 [markdown-clj "1.11.9"]
                 [com.googlecode.owasp-java-html-sanitizer/owasp-java-html-sanitizer "20220608.1" :exclusions [com.google.guava/guava]]
                 [com.fasterxml.jackson.core/jackson-core "2.15.2"]
                 [com.fasterxml.jackson.core/jackson-databind "2.15.2"]
                 [ring/ring-defaults "0.4.0"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-session-timeout "0.3.0"]
                 [selmer "1.12.59"]
                 [prismatic/schema "1.4.1"]
                 [yogthos/config "1.2.0"]
                 [environ "1.2.0"]
                 [com.sun.mail/jakarta.mail "2.0.1"]
                 [org.simplejavamail/simple-java-mail "8.3.1"]]

  :plugins [[lein-ancient "0.6.15"]
            [lein-shell "0.5.0"]]

  :min-lein-version "2.5.3"

  :main maksut.core

  :source-paths ["src/clj"]
  :test-paths ["test/clj"]

  :clean-targets ^{:protect false} ["target"
                                    ".ts-out"]

  :shell {:commands {"open" {:windows ["cmd" "/c" "start"]
                             :macosx  "open"
                             :linux   "xdg-open"}}}

  :jvm-opts ["-Dclojure.main.report=stderr"]

  :aliases {"server:dev"    ["with-profile" "dev" "run"]
            "build-report"  ["with-profile" "prod" "do"
                             ["run" "-m" "maksut" "target/build-report.html"]
                             ["shell" "open" "target/build-report.html"]]
            "lint"          ["with-profile" "dev" "do"
                             ["run" "-m" "clj-kondo.main" "--config" "oph-configuration/clj-kondo.config.edn" "--lint" "src"]]}

  :repl-options {:init-ns user}

  :profiles
  {:dev
            {:dependencies [[binaryage/devtools "1.0.7"]
                            [clj-kondo "2024.02.12"]
                            [reloaded.repl "0.2.4"]
                            [clj-http-fake "1.0.4"]]
             :source-paths ["dev/clj"]}

   :prod    {}

   :uberjar {:source-paths ["env/prod/clj"]
             :omit-source  false
             :aot          [maksut.core]
             :uberjar-name "maksut.jar"}}

  :repositories [["github" {:url "https://maven.pkg.github.com/Opetushallitus/packages"
                            :username "private-token"
                            :password :env/GITHUB_TOKEN}]
                 ["releases" {:url           "https://artifactory.opintopolku.fi/artifactory/oph-sade-release-local"
                              :sign-releases false
                              :snapshots     false}]
                 ["snapshots" {:url      "https://artifactory.opintopolku.fi/artifactory/oph-sade-snapshot-local"
                               :releases {:update :never}}]
                 ["ext-snapshots" {:url      "https://artifactory.opintopolku.fi/artifactory/ext-snapshot-local"
                                   :releases {:update :never}}]])
