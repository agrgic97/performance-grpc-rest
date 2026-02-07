import grpc from "k6/net/grpc";
import { check, sleep } from "k6";

const client = new grpc.Client();
client.load(["proto"], "payload.proto");

const GRPC_ADDR = __ENV.GRPC_ADDR || "localhost:9090";

export const options = {
    scenarios: {
        medium_only: {
            executor: "ramping-vus",
            startVUs: 0,
            stages: [
                { duration: "20s", target: 50 },

                { duration: "20s", target: 200 },

                { duration: "20s", target: 500 },

                { duration: "20s", target: 1000 },

                { duration: "20s", target: 0 },
            ],
        },
    },
};

export default function () {
    client.connect(GRPC_ADDR, { plaintext: true });

    const res = client.invoke(
        "bench.payload.PayloadService/GetLarge",
        {}
    );

    check(res, {
        "status OK": (r) => r && r.status === grpc.StatusOK,
    });

    client.close();
    sleep(1);
}
