const env = process.env;

module.exports = {
  development: {
    client: env.DB_DRIVER,
    connection: {
      host: env.DB_HOST,
      user: env.DB_USER,
      port: env.DB_PORT,
      password: env.DB_PASSWORD,
      database: env.DB_NAME,
    },
    migrations: {
      directory: './db/migrations',
      tableName: 'knex_migrations',
    },
    seeds: {
      directory: './db/seeds',
    },
  },
};
