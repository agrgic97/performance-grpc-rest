const express = require('express');
const {createPayloadController} = require("../controller/payloadController");
const router = express.Router();

const payloadController = createPayloadController()

router.get('/small', payloadController.small);
router.get('/medium', payloadController.medium);
router.get('/large', payloadController.large);

router.get('/json/small', payloadController.smallJson);
router.get('/json/medium', payloadController.mediumJson);

module.exports = router;