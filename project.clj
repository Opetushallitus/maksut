;; Riippuvuusperheiden versiot yhtenä totuutena. Leiningen ei tue Maven-BOM importia
;; (:scope "import"), joten artefaktit listataan eksplisiittisesti mutta versio jaetaan muuttujalla.
(def jackson-version "2.21.6")     ; CVE-2026-54512/54513 (CRITICAL); 2.19/2.20-linjoille ei backporttia
(def netty-version "4.2.17.Final") ; CVE-2026-44249, CVE-2026-75595 (CRITICAL) ym.; java-cas 2.3.0 / AHC 3.0.12
;; jetty: ring 1.9.1 (Jetty 9.4.57 EOL) -> ring 1.15.5 (Jetty 12.1.x, ee9-yhteensopivuuskerros).
;; Pinni 12.1.12:een: CVE-2026-2332 (fix 12.1.7) + CVE-2026-10050 (fix 12.1.10).
(def jetty-version "12.1.12")

(defproject maksut "0.1.0-SNAPSHOT"
  :managed-dependencies [[clj-commons/clj-yaml "1.0.29"]
                         [com.google.protobuf/protobuf-java "3.25.5"]
                         [commons-fileupload "1.6.0"]
                         [commons-io "2.14.0"]
                         [org.apache.commons/commons-compress "1.21"]
                         [org.apache.commons/commons-fileupload2-core "2.0.0-M4"]
                         [org.jsoup/jsoup "1.23.2"]
                         ;; Tietoturvapäivitykset 2026-09 (transitiiviset pinnit)
                         [com.fasterxml.jackson.core/jackson-core ~jackson-version]
                         [com.fasterxml.jackson.core/jackson-databind ~jackson-version]
                         [com.fasterxml.jackson.core/jackson-annotations "2.21"] ; annotations-linja ei käytä patch-numeroa
                         [io.netty/netty-buffer ~netty-version]
                         [io.netty/netty-common ~netty-version]
                         [io.netty/netty-codec ~netty-version]
                         [io.netty/netty-codec-base ~netty-version]
                         [io.netty/netty-codec-compression ~netty-version]
                         [io.netty/netty-codec-dns ~netty-version]
                         [io.netty/netty-codec-http ~netty-version]
                         [io.netty/netty-codec-http2 ~netty-version]
                         [io.netty/netty-codec-socks ~netty-version]
                         [io.netty/netty-handler ~netty-version]
                         [io.netty/netty-handler-proxy ~netty-version]
                         [io.netty/netty-resolver ~netty-version]
                         [io.netty/netty-resolver-dns ~netty-version]
                         [io.netty/netty-transport ~netty-version]
                         [io.netty/netty-transport-classes-epoll ~netty-version]
                         [io.netty/netty-transport-classes-kqueue ~netty-version]
                         [io.netty/netty-transport-native-unix-common ~netty-version]
                         [io.netty/netty-transport-native-epoll ~netty-version :classifier "linux-x86_64"]
                         [io.netty/netty-transport-native-epoll ~netty-version :classifier "linux-aarch_64"]
                         [io.netty/netty-transport-native-kqueue ~netty-version :classifier "osx-x86_64"]
                         [org.eclipse.jetty/jetty-server ~jetty-version]
                         [org.eclipse.jetty/jetty-http ~jetty-version]
                         [org.eclipse.jetty/jetty-io ~jetty-version]
                         [org.eclipse.jetty/jetty-util ~jetty-version]
                         [org.eclipse.jetty/jetty-security ~jetty-version]
                         [org.eclipse.jetty/jetty-session ~jetty-version]
                         [org.eclipse.jetty/jetty-xml ~jetty-version]
                         [org.eclipse.jetty/jetty-client ~jetty-version]
                         [org.eclipse.jetty/jetty-alpn-server ~jetty-version]
                         [org.eclipse.jetty/jetty-unixdomain-server ~jetty-version]
                         [org.eclipse.jetty.ee9/jetty-ee9-nested ~jetty-version]
                         [org.eclipse.jetty.ee9/jetty-ee9-servlet ~jetty-version]
                         [org.eclipse.jetty.ee9/jetty-ee9-security ~jetty-version]
                         [org.eclipse.jetty.ee9/jetty-ee9-webapp ~jetty-version]
                         [org.eclipse.jetty.ee9/jetty-ee9-xml ~jetty-version]
                         [org.eclipse.jetty.ee9.websocket/jetty-ee9-websocket-jetty-server ~jetty-version]
                         [org.eclipse.jetty.ee9.websocket/jetty-ee9-websocket-jetty-api ~jetty-version]
                         [org.eclipse.jetty.ee9.websocket/jetty-ee9-websocket-jetty-common ~jetty-version]
                         [org.eclipse.jetty.websocket/jetty-websocket-core-common ~jetty-version]
                         [org.eclipse.jetty.websocket/jetty-websocket-core-server ~jetty-version]
                         [org.eclipse.jetty.websocket/jetty-websocket-core-client ~jetty-version]]
  :dependencies [[org.clojure/clojure "1.11.4"]
                 [camel-snake-kebab "0.4.3"]
                 [cheshire "6.1.0"]
                 [clj-http "3.12.3"]
                 [software.amazon.awssdk/s3 "2.33.0"]
                 [com.taoensso/timbre "6.3.1"]
                 [com.fzakaria/slf4j-timbre "0.4.1"]
                 [timbre-ns-pattern-level "0.1.2"]
                 [com.stuartsierra/component "1.1.0"]
                 [clj-soup/clojure-soup "0.1.3"]
                 [org.flywaydb/flyway-core "11.20.1"]
                 [org.flywaydb/flyway-database-postgresql "11.20.1"]
                 [opiskelijavalinnat-utils/java-cas "2.3.0-SNAPSHOT"]
                 ;; buddy-core 1.12 -> pudottaa haavoittuvan bouncycastle *-jdk15on 1.70 -ketjun;
                 ;; buddy-core pinnaa jdk18on 1.78.1 -> pakotetaan 1.85 (CVE-2026-8763 ym.)
                 [buddy/buddy-core "1.12.0-430"]
                 [org.bouncycastle/bcprov-jdk18on "1.85"]
                 [org.bouncycastle/bcpkix-jdk18on "1.85"]
                 [org.bouncycastle/bcutil-jdk18on "1.85"]
                 [fi.vm.sade/auditlogger "9.2.7-SNAPSHOT"]
                 [fi.vm.sade.java-utils/java-properties "0.1.0-SNAPSHOT"]
                 [opiskelijavalinnat-utils.viestinvalitys/kirjasto "1.2.5-SNAPSHOT"]
                 [opiskelijavalinnat-utils/clj-ring-db-cas-session "1.0.0-SNAPSHOT"]
                 [hikari-cp "3.0.1"]
                 [metosin/reitit "0.6.0"]
                 [metosin/schema-tools "0.13.1"]
                 [metosin/ring-http-response "0.9.5"]
                 [metosin/ring-swagger-ui "5.9.0"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [org.clojure/core.match "1.1.0"]
                 [org.postgresql/postgresql "42.7.12"]
                 [com.layerware/hugsql "0.5.3"]
                 [ring/ring "1.15.5"]
                 [ring/ring-defaults "0.6.0"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-session-timeout "0.3.0"]
                 [selmer "1.12.59"]
                 [prismatic/schema "1.4.1"]
                 [yogthos/config "1.2.0"]
                 [environ "1.2.0"]
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
                            :password :env/GITHUB_TOKEN}]])
