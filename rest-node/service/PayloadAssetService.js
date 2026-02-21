const fs = require("fs");
const path = require("path");

class PayloadAssetService {
    constructor({ payloadDir } = {}) {
        this.payloadDir =
            (payloadDir && payloadDir.trim()) ||
            (process.env.PAYLOADS_DIR && process.env.PAYLOADS_DIR.trim());

        this.small = null;
        this.medium = null;
        this.large = null;

        this.smallJson = null;
        this.mediumJson = null;
    }

    load() {
        const dir = path.resolve(this.payloadDir);

        this.small = fs.readFileSync(path.join(dir, "small_100b.json"));
        this.medium = fs.readFileSync(path.join(dir, "medium_50kb.json"));
        this.large = fs.readFileSync(path.join(dir, "large_2mb.png"));

        this.smallJson = JSON.parse(this.small.toString());
        this.mediumJson = JSON.parse(this.medium.toString());

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

    getSmallJson() {
        return this.smallJson;
    }
    getMediumJson() {
        return this.mediumJson;
    }
}

module.exports = { PayloadAssetService };
