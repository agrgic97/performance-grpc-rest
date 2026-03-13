const grpc = require("@grpc/grpc-js");

function createPayloadServiceImpl(payloadAssetService) {
    if (!payloadAssetService) throw new Error("payloadAssetService is required");

    return {
        GetSmall(call, callback) {
            callback(null, payloadAssetService.getSmallObject());
        },

        GetMedium(call, callback) {
            callback(null, payloadAssetService.getMediumObject());
        },

        GetLarge(call, callback) {
            callback(null, payloadAssetService.getLargeObject());
        },

        GetLargeCompressed(call, callback) {
            const metadata = new grpc.Metadata();
            metadata.set("grpc-encoding", "gzip");
            call.sendMetadata(metadata);
            callback(null, payloadAssetService.getLargeObject());
        },

        StreamLarge(call) {
            const items = payloadAssetService.getLargeObject().items;
            for (const item of items) {
                call.write(item);
            }
            call.end();
        },
    };
}

module.exports = { createPayloadServiceImpl };
