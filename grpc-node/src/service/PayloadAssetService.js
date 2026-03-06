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
        this.smallObject = null;
        this.mediumObject = null;
    }

    load() {
        const dir = path.resolve(this.payloadDir);

        this.small = fs.readFileSync(path.join(dir, "small_100b.json"));
        this.medium = fs.readFileSync(path.join(dir, "medium_50kb.json"));
        this.large = fs.readFileSync(path.join(dir, "large_2mb.png"));

        this.smallObject = JSON.parse(this.small.toString());
        this.mediumObject = JSON.parse(this.medium.toString());

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

    getSmallObject() {
        return this.toSmallPayloadMessage(this.smallObject);
    }

    getMediumObject() {
        return this.toMediumPayloadMessage(this.mediumObject);
    }


    toSmallPayloadMessage(payload) {
        return {
            id: payload.id,
            protocol: payload.protocol,
            service: payload.service,
            payload_type: payload.payloadType,
            status: payload.status,
            fixed: payload.fixed,
        };
    }

    toMediumPayloadMessage(payload) {
        return {
            payload_type: payload.payloadType,
            description: payload.description,
            unit: payload.unit,
            items: payload.items,
            pad: payload.pad,
        };
    }
}

module.exports = {PayloadAssetService};
