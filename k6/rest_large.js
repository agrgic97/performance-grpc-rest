import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const PATH = __ENV.PATH_LARGE || "/api/payload/large";

export const options = {
    scenarios: {
        large_only: {
            executor: "ramping-vus",
            startVUs: 0,
            stages: [
                { duration: "20s", target: 50 },
            ],
            gracefulRampDown: "30s",
        },
    },
};

const params = {
    headers: {
        "Cache-Control": "no-store",
        Pragma: "no-cache",
    },
};

export default function () {
    const res = http.get(`${BASE_URL}${PATH}`, params);

    check(res, {
        "status 200": (r) => r.status === 200,
        "content-type image": (r) =>
            (r.headers["Content-Type"] || "").startsWith("image/"),
        "body not empty": (r) => (r.body || "").length > 10000,
    });

    sleep(1);
}
