const { SmallObject }  = require('../model/SmallObject');
const { MediumObject } = require('../model/MediumObject');
const { LargeObject }  = require('../model/LargeObject');

const BASE_URL = process.env.REST_NODE_URL || 'http://localhost:3000';

async function fetchFromServer(size) {
    const url = `${BASE_URL}/api/payload/${size}`;
    const res = await fetch(url);

    if (!res.ok) {
        const err = new Error(`REST upstream returned ${res.status} ${res.statusText} for ${url}`);
        err.status = res.status;
        throw err;
    }

    const data = await res.json();

    switch (size) {
        case 'small':  return new SmallObject(data);
        case 'medium': return new MediumObject(data);
        case 'large':  return new LargeObject(data);
        default: throw new Error(`Unknown size: ${size}`);
    }
}

module.exports = { fetchFromServer };
