'use strict';

const { SmallObject } = require('./SmallObject');

class LargeObject {
    constructor(data) {
        this.items = (data.items || []).map(item => new SmallObject(item));
    }
}

module.exports = { LargeObject };
