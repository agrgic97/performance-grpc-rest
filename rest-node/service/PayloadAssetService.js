class PayloadAssetService {
    constructor() {
        this.smallObject = null;
        this.mediumObject = null;
        this.largeObject = null;
    }

    load() {
        this.smallObject = this.#generateSmallObject();
        this.mediumObject = this.#generateMediumObject();
        this.largeObject = this.#generateLargeObject();

        return this;
    }

    getSmallObject() {
        return this.smallObject;
    }

    getMediumObject() {
        return this.mediumObject;
    }

    getLargeObject() {
        return this.largeObject;
    }

    #generateSmallObject() {
        return {
            id: 1,
            protocol: "http",
            service: "benchmark-service",
            payload_type: "small",
            status: "ok",
            fixed: true,
        };
    }

    #generateMediumObject() {
        let items = [];
        for (let i = 0; i < 40_000; i++) {
            items.push(i + 10_000_000)
        }

        return {
            payload_type: "medium",
            description: "Generated medium JSON payload",
            unit: "POJO",
            items: items,
        }
    }

    #generateLargeObject() {
        let items = [];
        for (let i = 0; i < 10; i++) {
            items.push(this.#generateMediumObject())
        }

        return {
            items: items,
        }
    }
}

module.exports = {PayloadAssetService};
