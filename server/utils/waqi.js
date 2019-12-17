const axios = require('axios');
const token = process.env.AQICN_TOKEN;
const baseURL = 'https://api.waqi.info';
const api = axios.create({
	baseURL,
});

function getCountryRankings() {
  return axios.get('https://waqi.info/rtdata/ranking/index.json')
    .then(({ data }) => data)
}

async function getStationFeed(city) {
  const data = await api.get(`/feed/${city}/`, {
		params: {
			token,
		},
	})
		.then(({ data }) => data);

	if (data.status === 'error') {
		throw new Error(data.data);
  }
  
  return data.data;
}

module.exports = {
  getCountryRankings,
  getStationFeed,
};
