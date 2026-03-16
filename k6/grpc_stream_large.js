import { Client, Stream } from "k6/net/grpc";
import { check } from "k6";
import { Trend } from "k6/metrics";
import { buildOptions } from "./config/options.js";

const client = new Client();
client.load(["proto"], "payload.proto");

const GRPC_ADDR = __ENV.BASE_URL;

export const options = buildOptions("grpc-stream-large");

const successLatency = new Trend("grpc_stream_success_duration", true);

export default function () {
    client.connect(GRPC_ADDR, { plaintext: true });

    let messageCount = 0;
    let streamError = false;
    const start = Date.now();

    const stream = new Stream(client, "bench.payload.PayloadService/StreamLarge", { discardResponseMessage: true });

    stream.on("data", function () {
        messageCount++;
    });

    stream.on("error", function (e) {
        streamError = true;
        console.error("Stream error: " + JSON.stringify(e));
    });

    stream.on("end", function () {
        const duration = Date.now() - start;

        const messagesOk = check(null, {
            "received exactly 10 messages": () => messageCount === 10,
        });
        const noErrors = check(null, {
            "no errors": () => streamError === false,
        });

        if (messagesOk && noErrors) {
            successLatency.add(duration);
        }

        client.close();
    });

    stream.write({});
}
