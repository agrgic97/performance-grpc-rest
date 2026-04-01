const express   = require('express');
const restProxy = require('../service/RestProxyService');
const grpcProxy = require('../service/GrpcProxyService');

const router = express.Router();

router.get('/rest/:size', async (req, res) => {
    const { size } = req.params;
    try {
        await restProxy.fetchFromServer(size);
        res.status(200).end();
    } catch (err) {
        console.error(`[client-node] REST proxy error (size=${size}): ${err.message}`, { status: err.status, stack: err.stack });
        res.status(err.status ?? 500).end();
    }
});

router.get('/grpc/:size', async (req, res) => {
    const { size } = req.params;
    try {
        await grpcProxy.fetchFromServer(size);
        res.status(200).end();
    } catch (err) {
        console.error(`[client-node] gRPC proxy error (size=${size}): ${err.message}`, { code: err.code, details: err.details, stack: err.stack });
        res.status(500).end();
    }
});

module.exports = router;
