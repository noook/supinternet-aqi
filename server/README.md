# Server

## Installation

```sh
cp .env.dist .env
# Then edit the file and set your credentials
```

```sh
# Install node dependencies
docker-compose run api npm i

# Run database migrations
docker-compose run api npm run knex migrate:latest
```

```sh
docker-compose up
# Requests and crons are logged
```

## Requests

Postman collection available in this folder to test the server.
