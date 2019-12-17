const crons = [
  {
    name: 'aqicn-history',
    job: require('./aqicn-history'),
  },
];

crons.forEach(cron => {
  cron.job();
});
