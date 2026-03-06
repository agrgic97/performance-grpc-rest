const ONE_GIB = 1024 * 1024 * 1024;
const FIFTY_KIB = 50 * 1024;

function createSmallPayloadObject() {
    return {
        id: 1,
        protocol: "gRPC",
        service: "payload-service",
        payloadType: "small",
        status: "ok",
        fixed: true,
    };
}

function createMediumPayloadObject(targetBytes = FIFTY_KIB) {
    const items = Array.from({ length: 512 }, (_, i) => ({
        id: i + 1,
        value: String((i + 1) * 1001),
    }));

    const base = {
        payloadType: "medium",
        description: "Generated medium JSON payload",
        unit: "bytes",
        items,
    };

    const baseSize = Buffer.byteLength(JSON.stringify(base));
    const padLen = Math.max(0, targetBytes - baseSize - Buffer.byteLength(',"pad":""'));

    return {
        ...base,
        pad: "7".repeat(padLen),
    };
}

function generateLargeJsonBuffer(targetBytes = ONE_GIB) {
    const prefix = '{"payloadType":"large","description":"Generated 1GiB numeric JSON payload","unit":"numbers","numbers":[';
    const suffix = ']}';

    const prefixLength = Buffer.byteLength(prefix);
    const suffixLength = Buffer.byteLength(suffix);

    if (targetBytes <= prefixLength + suffixLength + 1) {
        throw new Error("targetBytes is too small for a valid large JSON payload");
    }

    const buffer = Buffer.allocUnsafe(targetBytes);
    let offset = 0;

    function writeAscii(str) {
        offset += buffer.write(str, offset, "ascii");
    }

    writeAscii(prefix);

    const standardNumber = "1234567890";
    let firstNumber = true;

    while (offset + 1 + standardNumber.length + suffixLength < targetBytes) {
        writeAscii(firstNumber ? standardNumber : `,${standardNumber}`);
        firstNumber = false;
    }

    const remaining = targetBytes - offset - suffixLength;
    if (remaining <= 0) {
        throw new Error("Could not fit final numeric token into target payload size");
    }

    if (firstNumber) {
        buffer.fill(0x39, offset, offset + remaining);
        offset += remaining;
    } else {
        if (remaining < 2) {
            throw new Error("Remaining space is too small for comma + number token");
        }
        buffer[offset++] = 0x2c; // ','
        buffer.fill(0x39, offset, offset + remaining - 1);
        offset += remaining - 1;
    }

    writeAscii(suffix);

    if (offset !== targetBytes) {
        throw new Error(`Generated JSON size mismatch: expected ${targetBytes}, got ${offset}`);
    }

    return buffer;
}

class PayloadAssetService {
    constructor() {
        this.largeTargetBytes = Number(process.env.LARGE_JSON_SIZE_BYTES || ONE_GIB);

        this.small = null;
        this.medium = null;
        this.large = null;

        this.smallObject = null;
        this.mediumObject = null;
    }

    load() {
        this.smallObject = createSmallPayloadObject();
        this.mediumObject = createMediumPayloadObject();

        this.small = Buffer.from(JSON.stringify(this.smallObject));
        this.medium = Buffer.from(JSON.stringify(this.mediumObject));
        this.large = generateLargeJsonBuffer(this.largeTargetBytes);

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
