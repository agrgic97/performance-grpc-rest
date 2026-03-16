const RATE = parseInt(__ENV.RPS || "100", 10);
const TIME_UNIT = "1s";
const DURATION = "30s";
const PRE_ALLOCATED_VUS = Math.max(50, RATE * 2);
const MAX_VUS = Math.max(500, RATE * 10);

export function buildOptions(name = "run") {
    return {
        scenarios: {
            [name]: {
                executor: "constant-arrival-rate",
                rate: RATE,
                timeUnit: TIME_UNIT,
                duration: DURATION,
                preAllocatedVUs: PRE_ALLOCATED_VUS,
                maxVUs: MAX_VUS,
            },
        },
    };
}
