const config =
  process.env.CONFIG ||
  '../local-environment/oph-configurations/untuva/oph-configuration/maksut.config.edn'

module.exports = {
  apps: [
    {
      name: 'maksut-backend',
      script: 'lein',
      interpreter: '/bin/sh',
      args: ['server:dev'],
      env: {
        TIMBRE_NS_BLACKLIST: '["clj-timbre-auditlog.audit-log"]',
        CONFIG: config,
      },
      cwd: __dirname,
      log_file: 'logs/pm2/maksut-backend.log',
      pid_file: 'pids/maksut-backend.pid',
      combine_logs: true,
      min_uptime: 30000,
      max_restarts: 5,
      restart_delay: 4000,
      wait_ready: true,
      watch: false,
      exec_interpreter: 'none',
      exec_mode: 'fork',
    },
    {
      name: 'maksut-backend-test',
      script: 'lein',
      interpreter: '/bin/sh',
      args: ['server:dev'],
      env: {
        TIMBRE_NS_BLACKLIST: '["clj-timbre-auditlog.audit-log"]',
        CONFIG: 'oph-configuration/config.test.local-environment.edn',
      },
      cwd: __dirname,
      log_file: 'logs/pm2/maksut-backend-test.log',
      pid_file: 'pids/maksut-backend-test.pid',
      combine_logs: true,
      min_uptime: 30000,
      max_restarts: 5,
      restart_delay: 4000,
      wait_ready: true,
      watch: false,
      exec_interpreter: 'none',
      exec_mode: 'fork',
    },
  ],
}
