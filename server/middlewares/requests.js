const
  moment = require('moment'),
  chalk = require('chalk');

module.exports = (req, res, next) => {
  const reqStart = moment();
  const now = `[${chalk.green(moment().format('HH:mm:ss'))}]`;
  const method = chalk.magenta(req.method);
  const route = chalk.blue(req.path);

  res.on('finish', function () {
    const reqTime = chalk.rgb(153, 128, 250)(moment().diff(reqStart) + 'ms');
    const code = chalk.yellow(this.statusCode);
    console.log(`${now} ${code} ${method} request received on ${route} ${reqTime}`);
  })

  next();
}