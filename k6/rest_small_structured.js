import http from "k6/http";
import { check, sleep } from "k6";
import {buildOptions} from "./config/options.js";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const PATH = __ENV.PATH_SMALL_STRUCTURED || "/api/payload/json/small";

export const options = buildOptions("rest-small-structured");

export default function () {
    const res = http.get(`${BASE_URL}${PATH}`);

    check(res, {
        "status 200": (r) => r.status === 200,
        "content-type json": (r) =>
            (r.headers["Content-Type"] || "").includes("application/json"),
        "has parsed payload fields": (r) => {
            const body = r.json();
            return body && body.protocol && body.service && body.payloadType;
        },
    });

    sleep(1);
}
