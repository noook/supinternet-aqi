require('dotenv').config();

const
  express = require('express'),
  app = express(),
  bodyParser = require('body-parser'),
  chalk = require('chalk'),
  controllers = require('./controllers'),
  middlewares = require('./middlewares'),
  port = process.env.PORT || 3000;
  cors = require('cors');

console.clear();

require('./crons');

app.use(cors({
  credentials: true
}));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

Object.values(middlewares).forEach(middleware => {
  app.use(middleware);
});

Object.values(controllers).forEach(controller => {
  app.use(controller.route, controller.controller);
});

app.listen(port, () => {
  console.log(chalk.green(`Server started on port ${port}`));
});
