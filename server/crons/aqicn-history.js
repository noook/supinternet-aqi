
const
  moment = require('moment'),
  chalk = require('chalk'),
  queries = require('../utils/queries'),
  aqicn = require('../utils/waqi'),
  CronJob = require('cron').CronJob,
  cronName = chalk.yellow('AQICN Historisation');

module.exports = () => {
  new CronJob(process.env.CRON_INTERVAL , async function() {
    const now = `[${chalk.green(moment().format('HH:mm:ss'))}]`;
    console.log(`${now} Running cron ${cronName}`);
    const cities = await queries.cities();
    const promises = [];
    for (let i = 0; i < cities.length; i += 1) {
      const data = await aqicn.getStationFeed(`@${cities[i].idx}`);
      promises.push(queries.newPollutionRecord({
        city_id: cities[i].id,
        timestamp: data.time.s,
        values: JSON.stringify(data.iaqi),
      }));
    }
    await Promise.all(promises);
  }, null, true, 'Europe/Paris');
};
