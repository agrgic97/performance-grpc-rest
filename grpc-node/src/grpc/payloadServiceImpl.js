function createPayloadServiceImpl(payloadAssetService) {
    if (!payloadAssetService) throw new Error("payloadAssetService is required");

    function streamPayloadInChunks(call, payload, defaultPayloadId, defaultChunkSize) {
        const req = call.request || {};

        const payloadId = (req.payload_id || defaultPayloadId);
        const chunkSize = (req.chunk_size || defaultChunkSize);

        if (!Number.isInteger(chunkSize) || chunkSize <= 0) {
            call.destroy({
                code: 3,
                message: "chunkSize must be a positive integer",
            });
            return;
        }

        let seq = 0;

        for (let offset = 0; offset < payload.length; offset += chunkSize) {
            const end = Math.min(offset + chunkSize, payload.length);
            const chunk = payload.slice(offset, end);

            call.write({
                payload_id: payloadId,
                seq: seq++,
                data: chunk,
                eof: end >= payload.length,
            });
        }

        call.end();
    }

    return {
        GetSmall(call, callback) {
            callback(null, { payload: payloadAssetService.getSmall() });
        },

        GetMedium(call, callback) {
            callback(null, { payload: payloadAssetService.getMedium() });
        },

        GetLarge(call, callback) {
            callback(null, { payload: payloadAssetService.getLarge() });
        },
        StreamSmall(call) {
            streamPayloadInChunks(call, payloadAssetService.getSmall(), "small", 10);
        },

        StreamMedium(call) {
            streamPayloadInChunks(call, payloadAssetService.getMedium(), "medium", 2000);
        },

        StreamLarge(call) {
            streamPayloadInChunks(call, payloadAssetService.getLarge(), "large", 200_000);
        },
    };
}

module.exports = { createPayloadServiceImpl };
