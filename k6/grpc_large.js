import grpc from "k6/net/grpc";
import { check, sleep } from "k6";
import {buildOptions} from "./config/options.js";

const client = new grpc.Client();
client.load(["proto"], "payload.proto");

const GRPC_ADDR = __ENV.GRPC_ADDR || "localhost:9090";
let connected = false;

export const options = buildOptions("grpc-large");

export default function () {
    if (!connected) {
        client.connect(GRPC_ADDR, { plaintext: true });
        connected = true;
    }

    const res = client.invoke(
        "bench.payload.PayloadService/GetLarge",
        {}
    );

    check(res, {
        "status OK": (r) => r && r.status === grpc.StatusOK,
    });

    sleep(1);
}
