const RATE = 1;
const TIME_UNIT = "1s";
const DURATION = "1s";
const PRE_ALLOCATED_VUS = 3200;
const MAX_VUS = 20000;

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
