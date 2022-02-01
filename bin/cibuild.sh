#!/usr/bin/env bash

LEIN=$(command -v lein)
if [[ "${LEIN}" == "" ]]; then
  LEIN="./bin/lein"
fi

tsc() {
  npm run tsc:type-check
}

lint() {
  npm run lint:clj \
    && npm run lint:js
}

test-e2e() {
  npm run cypress:run:travis
}

test-lein() {
  CONFIG=oph-configuration/config.cypress.travis.edn \
    lein test
}

run-all-tests() {
  lint \
    && test-e2e
}

create-uberjar() {
  lein with-profile +prod uberjar
}

run-mocked-maksut() {
  docker kill maksut-e2e-db || true && docker rm -f maksut-e2e-db || true 2>&1 > /dev/null
  docker run --name maksut-e2e-db -d -e POSTGRES_PASSWORD=postgres_password -e POSTGRES_USER=postgres_user -e POSTGRES_DB=maksut -p 5432:5432 postgres:12-alpine
  CONFIG=oph-configuration/config.cypress.travis.edn java -jar target/maksut.jar &
  ./bin/wait-for.sh localhost:19033 -t 30
}

run-all-tests-and-create-uberjar() {
  tsc \
    && lint \
    && create-uberjar \
    && run-mocked-maksut \
    && test-e2e \
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
