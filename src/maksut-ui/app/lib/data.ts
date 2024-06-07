import { backendUrl } from "@/app/lib/configurations";
import { Lasku } from "@/app/lib/types";


export const fetchLaskutBySecret = async (secret: string | undefined): Promise<Array<Lasku>> => {
  const response = await fetch(`${backendUrl}/laskut-by-secret?secret=${secret}`, {cache: "no-cache"})
  if (response.ok) {
    return await response.json()
  } else {
    throw Error(response.statusText)
  }
}