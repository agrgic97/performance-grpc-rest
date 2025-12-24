const {resolve, join} = require("node:path");
const {readFileSync} = require("node:fs");

class PayloadAssetService {
    constructor({ payloadDir } = {}) {
        this.payloadDir =
            payloadDir?.trim() ||
            process.env.PAYLOADS_DIR?.trim();

        this.small = null;
        this.medium = null;
        this.large = null;
    }

    load() {
        const dir = resolve(this.payloadDir);

        const smallPath = join(dir, "small_100b.json");
        const mediumPath = join(dir, "medium_50kb.json");
        const largePath = join(dir, "large_2mb.png");

        this.small = readFileSync(smallPath);
        this.medium = readFileSync(mediumPath);
        this.large = readFileSync(largePath);

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
}

module.exports = {PayloadAssetService};
