const express = require('express');
const {createPayloadController} = require("../controller/payloadController");
const router = express.Router();

const payloadController = createPayloadController()

router.get('/small', payloadController.small);
router.get('/medium', payloadController.medium);
router.get('/large', payloadController.large);

module.exports = router;