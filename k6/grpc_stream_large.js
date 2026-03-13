import { Client, Stream } from "k6/net/grpc";
import { check, sleep } from "k6";
import { Trend, Counter } from "k6/metrics";
import { buildOptions } from "./config/options.js";

const client = new Client();
client.load(["proto"], "payload.proto");

const GRPC_ADDR = __ENV.BASE_URL;

export const options = buildOptions("grpc-stream-large");

export default function () {
    client.connect(GRPC_ADDR, { plaintext: true });

    let messageCount = 0;

    const stream = new Stream(client, "bench.payload.PayloadService/StreamLarge", { discardResponseMessage: true });

    stream.on("data", function () {
        messageCount++;
    });

    stream.on("error", function (e) {
        console.error("Stream error: " + JSON.stringify(e));
    });

    stream.on("end", function () {
        check(null, {
            "received exactly 10 messages": () => messageCount === 10,
        });

        client.close();
    });

    stream.write({});

    sleep(1);
}
