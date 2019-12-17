const db = require('../db/connection');

const insertIfNotExist = (params) => {
  return db('city')
    .orWhere(params)
    .then(rows => {
      if (!rows.length) {
        return db('city')
          .insert(params)
          .catch(console.error)
      }
    })
    .catch(console.error);
}

const cities = () => {
  return db('city');
}

const newPollutionRecord = (obj) => {
  const { city_id, timestamp } = obj
  return db('pollution_record')
    .andWhere({ city_id, timestamp })
    .then(rows => {
      if (!rows.length) {
        return db('pollution_record')
          .insert(obj)
          .catch(console.error);
      }
    })
    .catch(console.error)
};

const stationHistory = id => {
  return db('pollution_record')
    .leftJoin('city', 'city.id', 'pollution_record.city_id')
    .where('city.idx', id)
    .orderBy('timestamp', 'desc')
    .limit(20)
    .then(rows => {
      return rows.reverse();
    })
    .catch(console.error);
}

module.exports = {
  insertIfNotExist,
  cities,
  newPollutionRecord,
  stationHistory,
};
