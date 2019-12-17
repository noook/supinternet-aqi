exports.up = function(knex) {
  return knex.schema
    .createTable('city', function (table) {
      table.increments('id');
      table.integer('idx').notNullable();
      table.string('name', 255).notNullable();
    })
};

exports.down = function(knex) {
  return knex.schema
    .dropTable('city')
};