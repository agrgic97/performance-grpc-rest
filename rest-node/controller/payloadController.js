const {PayloadAssetService} = require('../service/PayloadAssetService')

function createPayloadController() {
    const payloadAssetService = new PayloadAssetService().load();

    return {
        small(req, res, next) {
            try {
                res.status(200).type("application/json").send(payloadAssetService.getSmallObject());
            } catch (err) {
                next(err);
            }
        },

        medium(req, res, next) {
            try {
                res.status(200).type("application/json").send(payloadAssetService.getMediumObject());
            } catch (err) {
                next(err);
            }
        },

        large(req, res, next) {
            try {
                res.status(200).type("application/json").send(payloadAssetService.getLargeObject());
            } catch (err) {
                next(err);
            }
        },
    };
}

module.exports = {createPayloadController};
