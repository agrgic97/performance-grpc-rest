const {PayloadAssetService} = require('../service/PayloadAssetService')

function createPayloadController() {
    const payloadAssetService = new PayloadAssetService().load();

    return {
        small(req, res) {
            res.status(200);
            res.type("application/json");
            res.send(payloadAssetService.getSmall());
        },

        medium(req, res) {
            res.status(200);
            res.type("application/json");
            res.send(payloadAssetService.getMedium());
        },

        large(req, res) {
            res.status(200);
            res.type("image/png");
            res.send(payloadAssetService.getLarge());
        },
    };
}

module.exports = {createPayloadController};
