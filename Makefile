NODE_MODULES = node_modules
PM2 = PM2_HOME=.pm2 npx pm2
DOCKER_COMPOSE=COMPOSE_PARALLEL_LIMIT=8 $(if $(DOCKER_SUDO),sudo )docker-compose

$(NODE_MODULES): package.json package-lock.json
	npm install
	touch $(NODE_MODULES)

start-docker:
	@$(DOCKER_COMPOSE) up -d maksut-nginx-local

start-docker-db:
	@$(DOCKER_COMPOSE) up -d maksut-db-local

start-docker-local:
	@$(DOCKER_COMPOSE) up -d maksut-nginx-local
	@$(DOCKER_COMPOSE) up -d maksut-db-local

start-docker-cypress:
	@$(DOCKER_COMPOSE) up -d maksut-nginx-local
	@$(DOCKER_COMPOSE) up -d maksut-e2e-db-local

kill-docker:
	@$(DOCKER_COMPOSE) kill

kill-docker-cypress:
	@$(DOCKER_COMPOSE) kill maksut-e2e-db-local

start: $(NODE_MODULES) start-docker
	@$(PM2) start pm2.config.js --only maksut-frontend
	@$(PM2) start pm2.config.js --only maksut-backend

start-local: $(NODE_MODULES) start-docker-local
	@$(PM2) start pm2.config.js --only maksut-frontend
	@$(PM2) start pm2.config.js --only maksut-backend

log: $(NODE_MODULES)
	@$(PM2) logs --timestamp

logs: log

status: $(NODE_MODULES)
	@$(PM2) status

kill: $(NODE_MODULES) kill-docker
	@$(PM2) kill

start-cypress: start-docker-cypress
	@$(PM2) start pm2.config.js --only maksut-frontend
	@$(PM2) start pm2.config.js --only maksut-backend-cypress

kill-cypress: kill-docker-cypress
	@$(PM2) stop pm2.config.js --only maksut-backend-cypress

restart: kill start
restart-local: kill start-local
restart-cypress: kill-cypress start-cypress

reload:
	@$(PM2) kill
	@$(PM2) start pm2.config.js --only maksut-frontend
	@$(PM2) start pm2.config.js --only maksut-backend
