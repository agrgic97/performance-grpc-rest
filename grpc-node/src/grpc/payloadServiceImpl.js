function createPayloadServiceImpl(payloadAssetService) {
    if (!payloadAssetService) throw new Error("payloadAssetService is required");

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
    };
}

module.exports = { createPayloadServiceImpl };
