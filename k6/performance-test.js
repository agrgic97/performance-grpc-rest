import http from "k6/http";
import { check } from "k6";
import { buildOptions } from "./config/options.js";

const PROTOCOL = __ENV.PROTOCOL;
const PAYLOAD = __ENV.PAYLOAD;
const BASE_URL = __ENV.BASE_URL;

const scenarioName = `${PROTOCOL}-${PAYLOAD}`;

export const options = buildOptions(scenarioName);

export default function () {
    const res = http.get(`${BASE_URL}/${PROTOCOL}/${PAYLOAD}`);

    check(res, {
        "status 200": (r) => r.status === 200,
    });
}
