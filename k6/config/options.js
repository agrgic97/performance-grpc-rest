const TIME_UNIT = "1s";
const DEFAULT_RPS = Number(__ENV.RPS || 100);
const DEFAULT_DURATION = __ENV.TEST_DURATION || "30s";

function vusForRps(rps) {
    if (rps <= 100) {
        return { preAllocatedVUs: 50, maxVUs: 200 };
    }

    if (rps <= 1000) {
        return { preAllocatedVUs: 250, maxVUs: 1000 };
    }

    if (rps <= 5000) {
        return { preAllocatedVUs: 1000, maxVUs: 5000 };
    }

    if (rps <= 10000) {
        return { preAllocatedVUs: 2000, maxVUs: 10000 };
    }

    if (rps <= 50000) {
        return { preAllocatedVUs: 10000, maxVUs: 50000 };
    }

    return { preAllocatedVUs: 20000, maxVUs: 100000 };
}

export function buildOptions(name = "run") {
    const rps = Number(__ENV.RPS || DEFAULT_RPS);
    const duration = __ENV.TEST_DURATION || DEFAULT_DURATION;
    const { preAllocatedVUs, maxVUs } = vusForRps(rps);

    return {
        scenarios: {
            [name]: {
                executor: "constant-arrival-rate",
                rate: rps,
                timeUnit: TIME_UNIT,
                duration,
                preAllocatedVUs,
                maxVUs,
            },
        },
    };
}
