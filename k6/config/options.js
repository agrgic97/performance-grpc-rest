const VUS = parseInt(__ENV.VUS);
const DURATION = __ENV.DURATION || "20s";

export function buildOptions(name = "run") {
    return {
        scenarios: {
            [name]: {
                executor: 'constant-vus',
                vus: VUS,
                duration: DURATION,
            }
        }
    }
 }