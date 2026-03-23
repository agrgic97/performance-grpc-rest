'use strict';

const { SmallObject } = require('./SmallObject');

class MediumObject {
    constructor(data) {
        this.items = (data.items || []).map(item => new SmallObject(item));
    }
}

module.exports = { MediumObject };
