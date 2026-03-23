const grpc = require("@grpc/grpc-js");

function grpcError(code, message) {
    return { code, details: message };
}

function createPayloadServiceImpl(payloadAssetService) {
    if (!payloadAssetService) throw new Error("payloadAssetService is required");

    return {
        GetSmall(call, callback) {
            try {
                callback(null, payloadAssetService.getSmallObject());
            } catch (err) {
                console.error("[grpc-node] GetSmall error:", err);
                callback(grpcError(grpc.status.INTERNAL, err.message));
            }
        },

        GetMedium(call, callback) {
            try {
                callback(null, payloadAssetService.getMediumObject());
            } catch (err) {
                console.error("[grpc-node] GetMedium error:", err);
                callback(grpcError(grpc.status.INTERNAL, err.message));
            }
        },

        GetLarge(call, callback) {
            try {
                callback(null, payloadAssetService.getLargeObject());
            } catch (err) {
                console.error("[grpc-node] GetLarge error:", err);
                callback(grpcError(grpc.status.INTERNAL, err.message));
            }
        },

        GetLargeCompressed(call, callback) {
            try {
                const metadata = new grpc.Metadata();
                metadata.set("grpc-encoding", "gzip");
                call.sendMetadata(metadata);
                callback(null, payloadAssetService.getLargeObject());
            } catch (err) {
                console.error("[grpc-node] GetLargeCompressed error:", err);
                callback(grpcError(grpc.status.INTERNAL, err.message));
            }
        },

        StreamLarge(call) {
            try {
                for (let i = 0; i < 10; i++) {
                    call.write(payloadAssetService.getMediumObject());
                }
                call.end();
            } catch (err) {
                console.error("[grpc-node] StreamLarge error:", err);
                call.destroy(err);
            }
        },
    };
}

module.exports = { createPayloadServiceImpl };