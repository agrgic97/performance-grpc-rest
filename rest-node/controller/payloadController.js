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

        smallJson(req, res) {
            res.status(200);
            res.type("application/json");
            res.send(payloadAssetService.getSmallJson());
        },

        mediumJson(req, res) {
            res.status(200);
            res.type("application/json");
            res.send(payloadAssetService.getMediumJson());
        },
    };
}

module.exports = {createPayloadController};
