#!/usr/bin/env bash

LEIN=$(command -v lein)
if [[ "${LEIN}" == "" ]]; then
  LEIN="./bin/lein"
fi

lint() {
  npm run lint:clj
}

test-lein() {
  CONFIG=oph-configuration/config.test.github.edn \
    lein test
}

run-all-tests() {
  lint \
    && run-mocked-maksut \
    && test-lein
}

create-uberjar() {
  lein with-profile +prod uberjar
}

run-mocked-maksut() {
  docker kill maksut-e2e-db || true && docker rm -f maksut-e2e-db || true 2>&1 > /dev/null
  docker run --name maksut-e2e-db -d -e POSTGRES_PASSWORD=postgres_password -e POSTGRES_USER=postgres_user -e POSTGRES_DB=maksut -p 5432:5432 postgres:15-alpine
  docker run --name maksut-maksut-wiremock-local -d -p 9040:8080 wiremock/wiremock:3.4.2
  docker run --name maksut-mailcatcher-local -d -p 1025:1025 -p 1080:1080 sj26/mailcatcher
  CONFIG=oph-configuration/config.test.github.edn java -jar target/maksut.jar &
  ./bin/wait-for.sh localhost:19033 -t 30
}

run-all-tests-and-create-uberjar() {
  lint \
    && create-uberjar \
    && run-mocked-maksut \
    && test-lein
}

COMMAND=$1

case $COMMAND in
  "run-all-tests" )
    run-all-tests
    ;;
  "run-all-tests-and-create-uberjar" )
    run-all-tests-and-create-uberjar
    ;;
  "lint" )
    lint
    ;;
  "test-e2e" )
    test-e2e
    ;;
  "run-mocked-maksut" )
    run-mocked-maksut
    ;;
esac
