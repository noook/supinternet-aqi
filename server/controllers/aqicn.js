const
	axios = require('axios'),
	token = process.env.AQICN_TOKEN,
	express = require('express'),
	{ getCountryRankings } = require('../utils/waqi'),
	queries = require('../utils/queries'),
	app = express.Router();

const baseURL = 'https://api.waqi.info';
const api = axios.create({
	baseURL,
});

app.get('/search', async (req, res, next) => {
	const { q: keyword } = req.query;

	if (!keyword) {
		return res.status(400).send({ message: 'q param is missing' });
	}

	const data = await api.get('/search/', {
		params: {
			token,
			keyword,
		},
	})
		.then(({ data }) => data.data);

	const results = data.map(el => ({
		uid: el.uid,
		aqi: el.aqi,
		time: {
			timezome: el.time.tz,
			posix: el.time.vtime,
			datetime: el.time.stime,
		},
		station: {
			name: el.station.name,
			url: el.station.url,
			geo: {
				lat: el.station.geo[0],
				long: el.station.geo[1],
			},
			country: el.station.country,
		},
	}));

	res.status(200).send({ results });
});

app.get('/gps', async (req, res, next) => {
	const { lat, lng } = req.query;

	if (!(lat && lng)) {
		return res.status(400).send({ messsage: 'lat or lng parameter is missing' })
	}

	const data = await api.get(`/feed/geo:${lat};${lng}/`, {
		params: {
			token,
		},
	})
		.then(({ data }) => data.data);

	res.status(200).send(data);
});

app.get('/station', async (req, res, next) => {
	const { city } = req.query;

	if (!city) {
		return res.status(400).send({ messsage: 'city parameter is missing' })
	}
	
	const data = await api.get(`/feed/${city}/`, {
		params: {
			token,
		},
	})
		.then(({ data }) => data);

	if (data.status === 'error') {
		return res.status(404).send(data.data);
	}

	queries.insertIfNotExist({ idx: data.data.idx, name: data.data.city.name });

	res.status(200).send(data.data);
});

app.get('/station/history', async (req, res, next) => {
	const { city } = req.query;

	if (!city) {
		return res.status(400).send({ messsage: 'city parameter is missing' })
	}
	
	const result = await queries.stationHistory(city);

	res.status(200).send(result);
});

app.get('/map/bounds', async (req, res, next) => {
	const { latlng } = req.query;
	// 39.379436,116.091230,40.235643,116.7843
	if (!latlng) {
		return res.status(400).send({ messsage: 'Coordinates parameter is missing' })
	}
	
	const data = await api.get('/map/bounds/', {
		params: {
			token,
			latlng,
		},
	})
		.then(({ data }) => data);

	if (data.status === 'error') {
		return res.status(404).send(data.data);
	}

	res.status(200).send({ results: data.data });
});

app.get('/rankings', async (req, res, next) => {
	let { country } = req.query;

	if (!country) {
		return res.status(400).send({ messsage: 'Country parameter is missing' })
	}

	country = country.toUpperCase();
	const { cities } = await getCountryRankings();
	const result = cities.find(entry => entry.country === country);

	if (typeof result === 'undefined') {
		return res.status(404).send({ message: 'Country not found' });
	}

	res.status(200).send(result);
});

module.exports = app;
