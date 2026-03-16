import grpc from "k6/net/grpc";
import { check } from "k6";
import {buildOptions} from "./config/options.js";

const client = new grpc.Client();
client.load(["proto"], "payload.proto");

const GRPC_ADDR = __ENV.BASE_URL;

export const options = buildOptions("grpc-medium");

export default function () {
    client.connect(GRPC_ADDR, { plaintext: true });

    const res = client.invoke(
        "bench.payload.PayloadService/GetMedium",
        {}, {
            discardResponseMessage: true
        }
    );


    check(res, {
        "status OK": (r) => r && r.status === grpc.StatusOK,
        "error is null": (r) => r && r.error === null,
    });

    client.close();
}
