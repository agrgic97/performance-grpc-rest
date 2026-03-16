import http from "k6/http";
import { check } from "k6";
import {buildOptions} from "./config/options.js";

const BASE_URL = __ENV.BASE_URL;
const PATH = "/api/payload/medium";

export const options = buildOptions("rest-medium");

export default function () {
    const res = http.get(`${BASE_URL}${PATH}`, {
        discardResponseBodies: true
    });

    check(res, {
        "status 200": (r) => r.status === 200,
    });
}
