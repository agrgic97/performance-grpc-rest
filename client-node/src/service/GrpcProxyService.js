'use strict';

const path        = require('path');
const grpc        = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const { SmallObject }  = require('../model/SmallObject');
const { MediumObject } = require('../model/MediumObject');
const { LargeObject }  = require('../model/LargeObject');

const PROTO_PATH = path.resolve(__dirname, '../../proto/payload.proto');
const packageDef = protoLoader.loadSync(PROTO_PATH, {
    keepCase: true,
    longs:    Number,
    enums:    String,
    defaults: true,
    oneofs:   true,
});
const proto   = grpc.loadPackageDefinition(packageDef);
const address = process.env.GRPC_NODE_ADDR || 'localhost:4000';
const stub    = new proto.bench.payload.PayloadService(
    address,
    grpc.credentials.createInsecure()
);

function callUnary(method) {
    return new Promise((resolve, reject) => {
        stub[method]({}, (err, response) => {
            if (err) {
                err.message = `gRPC ${method} failed [code=${err.code}]: ${err.details || err.message}`;
                reject(err);
            } else {
                resolve(response);
            }
        });
    });
}

function callServerStream(method) {
    return new Promise((resolve, reject) => {
        const call  = stub[method]({});
        const items = [];
        call.on('data',  msg => items.push(msg));
        call.on('error', (err) => {
            err.message = `gRPC stream ${method} failed [code=${err.code}]: ${err.details || err.message}`;
            reject(err);
        });
        call.on('end',   () => resolve(items));
    });
}

async function fetch(size) {
    switch (size) {
        case 'small': {
            const data = await callUnary('GetSmall');
            return new SmallObject(data);
        }
        case 'medium': {
            const data = await callUnary('GetMedium');
            return new MediumObject(data);
        }
        case 'large': {
            const data = await callUnary('GetLarge');
            return new LargeObject(data);
        }
        case 'large_compressed': {
            const data = await callUnary('GetLargeCompressed');
            return new LargeObject(data);
        }
        case 'stream_large': {
            const chunks = await callServerStream('StreamLarge');
            return chunks.map(d => new MediumObject(d));
        }
        default:
            throw new Error(`Unknown size: ${size}`);
    }
}

module.exports = { fetch };
