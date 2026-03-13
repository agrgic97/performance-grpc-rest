import grpc from "k6/net/grpc";
import { check, sleep } from "k6";
import { Trend } from "k6/metrics";
import { buildOptions } from "./config/options.js";

const client = new grpc.Client();
client.load(["proto"], "payload.proto");

const GRPC_ADDR = __ENV.BASE_URL;

export const options = buildOptions("grpc-large-compressed");

const successLatency = new Trend("grpc_success_req_duration", true);

export default function () {
    client.connect(GRPC_ADDR, { plaintext: true });

    const start = Date.now();
    const res = client.invoke(
        "bench.payload.PayloadService/GetLargeCompressed",
        {},
    );
    const duration = Date.now() - start;

    console.log(res)

    const ok = check(res, {
        "status OK": (r) => r && r.status === grpc.StatusOK,
        "error is null": (r) => r && r.error === null,
    });

    if (ok) {
        successLatency.add(duration);
    }

    client.close();
    sleep(1);
}
