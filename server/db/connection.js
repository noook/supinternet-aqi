const knexfile = require('../knexfile');
const env = process.env.NODE_ENV;

module.exports = require('knex')(knexfile[env || Object.values(knexfile)[0]]);
