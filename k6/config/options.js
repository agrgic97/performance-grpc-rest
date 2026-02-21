const START_RATE = parseInt(__ENV.START_RATE || "10", 10);
const TIME_UNIT = __ENV.TIME_UNIT || "1s";
const STAGE_DURATION = __ENV.STAGE_DURATION || "20s";
const PRE_ALLOCATED_VUS = parseInt(__ENV.PRE_ALLOCATED_VUS || "50", 10);
const MAX_VUS = parseInt(__ENV.MAX_VUS || "2000", 10);

export function buildOptions(name = "run") {
    return {
        scenarios: {
            [name]: {
                executor: "constant-arrival-rate",
                rate: START_RATE,
                timeUnit: TIME_UNIT,
                duration: STAGE_DURATION,
                preAllocatedVUs: PRE_ALLOCATED_VUS,
                maxVUs: MAX_VUS,
            },
        },
    };
}
