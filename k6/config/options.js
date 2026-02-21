const START_RATE = 0;
const TIME_UNIT = "1s";
const PRE_ALLOCATED_VUS = 50;
const MAX_VUS = 2000;

const STAGES = [
    { target: 10, duration: "20s" },
    { target: 50, duration: "20s" },
    { target: 200, duration: "20s" },
    { target: 500, duration: "20s" },
    { target: 1000, duration: "20s" },
];

export function buildOptions(name = "run") {
    return {
        scenarios: {
            [name]: {
                executor: "ramp-arrival-rate",
                startRate: START_RATE,
                timeUnit: TIME_UNIT,
                stages: STAGES,
                preAllocatedVUs: PRE_ALLOCATED_VUS,
                maxVUs: MAX_VUS,
            },
        },
    };
}
