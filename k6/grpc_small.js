import grpc from "k6/net/grpc";
import { check, sleep } from "k6";

const client = new grpc.Client();
client.load(["proto"], "payload.proto");

const GRPC_ADDR = __ENV.GRPC_ADDR || "localhost:9090";

export const options = {
    scenarios: {
        small_only: {
            executor: "ramping-vus",
            startVUs: 0,
            stages: [
                { duration: "20s", target: 1000 },
            ],
            gracefulRampDown: "30s",
        },
    },
};

export default function () {
    client.connect(GRPC_ADDR, { plaintext: true });

    const res = client.invoke(
        "bench.payload.PayloadService/GetSmall",
        {}
    );

    check(res, {
        "status OK": (r) => r && r.status === grpc.StatusOK,
    });

    client.close();
    sleep(1);
}
