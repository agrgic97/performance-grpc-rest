import grpc from "k6/net/grpc";
import { check, sleep } from "k6";

const client = new grpc.Client();
client.load(["proto"], "payload.proto");

const GRPC_ADDR = __ENV.GRPC_ADDR || "localhost:9090";

const CHUNK_SIZE = parseInt(__ENV.CHUNK_SIZE || "200000", 10);

export const options = {
    scenarios: {
        stream_large_only: {
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
    if(__ITER === 0) {
        client.connect(GRPC_ADDR, { plaintext: true });
    }

    let bytes = 0;
    let chunks = 0;
    let sawEof = false;
    let streamError = null;

    const stream = new grpc.Stream(client, "bench.payload.PayloadService/StreamLarge");

    stream.on("data", (msg) => {
        if (msg && msg.data) {
            bytes += msg.data.length || 0;
        }
        chunks += 1;

        if (msg && msg.eof === true) {
            sawEof = true;
        }
    });

    stream.on("error", (e) => {
        streamError = e;
    });

    stream.write({
        payloadId: "large",
        chunkSize: CHUNK_SIZE,
    });

    stream.on('end', () => {
        check(null, {
            "stream had no error": () => streamError === null,
            "received at least 1 chunk": () => chunks > 0,
            "received some bytes": () => bytes > 0,
            "saw eof marker": () => sawEof === true,
        });
    });

    stream.end();

    sleep(1);
}