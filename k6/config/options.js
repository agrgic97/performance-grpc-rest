const RATE = 100;
const TIME_UNIT = "1s";
const DURATION = "30s";
const PRE_ALLOCATED_VUS = 120;
const MAX_VUS = 500;

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
