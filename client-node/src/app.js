const express = require('express');
const proxyRouter = require('./routes/proxyRouter');

const PORT = process.env.PORT ? Number(process.env.PORT) : 3005;

const app = express();
app.use('/', proxyRouter);

app.use((err, req, res, _next) => {
    console.error(`[client-node] Unhandled error on ${req.method} ${req.path}:`, err);
    res.status(500).end();
});

app.listen(PORT, () => {
    console.log(`client-node listening on port ${PORT}`);
});

process.on('unhandledRejection', (reason) => {
    console.error('[client-node] Unhandled promise rejection:', reason);
});

process.on('uncaughtException', (err) => {
    console.error('[client-node] Uncaught exception:', err);
    process.exit(1);
});
