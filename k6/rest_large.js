import http from "k6/http";
import { check } from "k6";
import {buildOptions} from "./config/options.js";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const PATH = __ENV.PATH_LARGE || "/api/payload/large";

export const options = buildOptions("rest-large");

export default function () {
    const res = http.get(`${BASE_URL}${PATH}`);

    check(res, {
        "status 200": (r) => r.status === 200,
        "content-type image": (r) =>
            (r.headers["Content-Type"] || "").startsWith("image/"),
        "body not empty": (r) => (r.body || "").length > 10000,
    });
}
