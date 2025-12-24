require("dotenv").config();
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');

const payloadRouter = require('./routes/payloadRouter');
const {noCache} = require("./middleware/noCache");

const app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use(noCache)

app.use('/api/payload', payloadRouter);

module.exports = app;
