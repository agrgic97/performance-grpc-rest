class SmallPayload {
    constructor({ id, protocol, service, payloadType, status, fixed }) {
        this.id = id;
        this.protocol = protocol;
        this.service = service;
        this.payloadType = payloadType;
        this.status = status;
        this.fixed = fixed;
    }

    static fromJson(json) {
        return new SmallPayload(json);
    }
}

class MediumPayloadItem {
    constructor({ id, value }) {
        this.id = id;
        this.value = value;
    }
}

class MediumPayload {
    constructor({ payloadType, description, unit, items = [] }) {
        this.payloadType = payloadType;
        this.description = description;
        this.unit = unit;
        this.items = items.map((item) => new MediumPayloadItem(item));
    }

    static fromJson(json) {
        return new MediumPayload(json);
    }
}

module.exports = {
    SmallPayload,
    MediumPayload,
};
