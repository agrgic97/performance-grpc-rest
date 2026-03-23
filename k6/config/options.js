const VUS = parseInt(__ENV.VUS || "10", 10);
const DURATION = "30s";

export function buildOptions(name = "run") {
    return {
        scenarios: {
            [name]: {
                executor: "constant-vus",
                vus: VUS,
                duration: DURATION,
            },
        },
    };
}
