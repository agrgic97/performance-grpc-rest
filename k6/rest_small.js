import http from "k6/http";
import { check, sleep } from "k6";

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080";
const PATH = __ENV.PATH_SMALL || "/api/payload/small";

export const options = {
    scenarios: {
        small_only: {
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
    const res = http.get(`${BASE_URL}${PATH}`);

    check(res, {
        "status 200": (r) => r.status === 200,
        "content-type json": (r) =>
            (r.headers["Content-Type"] || "").includes("application/json"),
    });

    sleep(1);
}
