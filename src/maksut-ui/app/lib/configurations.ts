export const backendUrl: string = process.env.MAKSUT_URL || "https://localhost:9000/maksut/api"

export const isDev: boolean = (process.env.DEVELOPMENT === 'true') || true