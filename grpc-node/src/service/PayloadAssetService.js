const fs = require("fs");
const path = require("path");

class PayloadAssetService {
    constructor({payloadDir} = {}) {
        this.payloadDir =
            (payloadDir && payloadDir.trim()) ||
            (process.env.PAYLOADS_DIR && process.env.PAYLOADS_DIR.trim());

        this.small = null;
        this.medium = null;
        this.large = null;
        this.smallStructured = null;
        this.mediumStructured = null;
    }

    load() {
        const dir = path.resolve(this.payloadDir);

        this.small = fs.readFileSync(path.join(dir, "small_100b.json"));
        this.medium = fs.readFileSync(path.join(dir, "medium_50kb.json"));
        this.large = fs.readFileSync(path.join(dir, "large_2mb.png"));

        const smallObject = JSON.parse(this.small.toString("utf8"));
        const mediumObject = JSON.parse(this.medium.toString("utf8"));

        this.smallStructured = {
            ...smallObject,
            payload_type: smallObject.payloadType,
        };
        delete this.smallStructured.payloadType;

        this.mediumStructured = {
            ...mediumObject,
            payload_type: mediumObject.payloadType,
        };
        delete this.mediumStructured.payloadType;

        return this;
    }

    getSmall() {
        return this.small;
    }

    getMedium() {
        return this.medium;
    }

    getLarge() {
        return this.large;
    }

    getSmallStructured() {
        return this.smallStructured;
    }

    getMediumStructured() {
        return this.mediumStructured;
    }
}

module.exports = {PayloadAssetService};
