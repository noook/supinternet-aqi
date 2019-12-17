exports.up = function(knex) {
  return knex.schema
    .createTable('pollution_record', function(table) {
      table.increments('id');
      table.integer('city_id');
      table.foreign('city_id').references('city.id');
      table.timestamp('timestamp');
      table.json('values');
    });
};

exports.down = function(knex) {
  return knex.schema
    .dropTable('pollution_record');
};
