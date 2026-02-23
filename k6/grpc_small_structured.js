import grpc from "k6/net/grpc";
import { check, sleep } from "k6";
import {buildOptions} from "./config/options.js";

const client = new grpc.Client();
client.load(["proto"], "payload.proto");

const GRPC_ADDR = __ENV.GRPC_ADDR || "localhost:9090";

export const options = buildOptions("grpc-small-structured");

export default function () {
    client.connect(GRPC_ADDR, { plaintext: true });

    const res = client.invoke(
        "bench.payload.PayloadService/GetSmallStructured",
        {}
    );

    check(res, {
        "status OK": (r) => r && r.status === grpc.StatusOK,
        "has structured fields": (r) => r && r.message && r.message.protocol && r.message.service,
    });

    client.close();
    sleep(1);
}
