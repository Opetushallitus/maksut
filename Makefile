PM2 = PM2_HOME=.pm2 npx pm2
DOCKER_COMPOSE=COMPOSE_PARALLEL_LIMIT=8 $(if $(DOCKER_SUDO),sudo )docker compose

start-docker:
	@$(DOCKER_COMPOSE) up -d maksut-nginx-local

start-docker-db:
	@$(DOCKER_COMPOSE) up -d maksut-db-local

start-docker-local:
	@$(DOCKER_COMPOSE) up -d maksut-nginx-local
	@$(DOCKER_COMPOSE) up -d maksut-db-local
	@$(DOCKER_COMPOSE) up -d maksut-mailcatcher-local
	@$(DOCKER_COMPOSE) up -d maksut-wiremock-local

start-docker-test: start-docker-local
	@$(DOCKER_COMPOSE) up -d maksut-e2e-db-local

kill-docker:
	@$(DOCKER_COMPOSE) kill

kill-docker-test:
	@$(DOCKER_COMPOSE) kill maksut-e2e-db-local

start: start-docker
	@$(PM2) start pm2.config.js --only maksut-backend

start-local: start-docker-local
	@$(PM2) start pm2.config.js --only maksut-backend

log:
	@$(PM2) logs --timestamp

logs: log

status:
	@$(PM2) status

kill: kill-docker
	@$(PM2) kill

start-test: start-docker-test
	@$(PM2) start pm2.config.js --only maksut-backend-test

kill-test: kill-docker-test
	@$(PM2) stop pm2.config.js --only maksut-backend-test

restart: kill start
restart-local: kill start-local
restart-test: kill-test start-test

reload:
	@$(PM2) kill
	@$(PM2) start pm2.config.js --only maksut-backend
