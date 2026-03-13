const {PayloadAssetService} = require('../service/PayloadAssetService')

function createPayloadController() {
    const payloadAssetService = new PayloadAssetService().load();

    return {
        small(req, res) {
            res.status(200);
            res.type("application/json");
            res.send(payloadAssetService.getSmallObject());
        },

        medium(req, res) {
            res.status(200);
            res.type("application/json");
            res.send(payloadAssetService.getMediumObject());
        },

        large(req, res) {
            res.status(200);
            res.type("application/json");
            res.send(payloadAssetService.getLargeObject());
        },
    };
}

module.exports = {createPayloadController};
