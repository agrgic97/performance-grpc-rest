require("dotenv").config();

const path = require("path");
const grpc = require("@grpc/grpc-js");
const protoLoader = require("@grpc/proto-loader");

const { PayloadAssetService } = require("./service/PayloadAssetService");
const { createPayloadServiceImpl } = require("./grpc/payloadServiceImpl");

const PORT = process.env.PORT ? Number(process.env.PORT) : 4000;

const PROTO_PATH = path.resolve(__dirname, "../proto/payload.proto");
const packageDef = protoLoader.loadSync(PROTO_PATH, {
    keepCase: true,
    longs: Number,
    enums: String,
    defaults: true,
    oneofs: true,
});
const proto = grpc.loadPackageDefinition(packageDef);

const payloadAssetService = new PayloadAssetService().load();

const server = new grpc.Server();
server.addService(
    proto.bench.payload.PayloadService.service,
    createPayloadServiceImpl(payloadAssetService)
);

server.bindAsync(
    `0.0.0.0:${PORT}`,
    grpc.ServerCredentials.createInsecure(),
    (err, port) => {
        if (err) {
            console.error("[grpc-node] Failed to bind gRPC server:", err);
            process.exit(1);
        }
        console.log(`[grpc-node] gRPC server listening on 0.0.0.0:${port}`);
    }
);

process.on('unhandledRejection', (reason) => {
    console.error('[grpc-node] Unhandled promise rejection:', reason);
});

process.on('uncaughtException', (err) => {
    console.error('[grpc-node] Uncaught exception:', err);
    process.exit(1);
});
