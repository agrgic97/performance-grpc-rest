require("dotenv").config();
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');

const payloadRouter = require('./routes/payloadRouter');

const app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/api/payload', payloadRouter);

app.use((err, req, res, _next) => {
    console.error(`[rest-node] Unhandled error on ${req.method} ${req.path}:`, err);
    res.status(500).json({ error: 'Internal server error' });
});

process.on('unhandledRejection', (reason) => {
    console.error('[rest-node] Unhandled promise rejection:', reason);
});

process.on('uncaughtException', (err) => {
    console.error('[rest-node] Uncaught exception:', err);
    process.exit(1);
});

module.exports = app;
