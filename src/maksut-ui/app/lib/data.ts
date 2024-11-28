import { backendUrl } from "@/app/lib/configurations";
import { Lasku } from "@/app/lib/types";
import { notFound } from "next/navigation";

export const fetchLaskutBySecret = async (secret: string | undefined): Promise<{laskut: Array<Lasku>, contact?: string}> => {
  const response = await fetch(`${backendUrl}/laskut-by-secret?secret=${secret}`, {cache: "no-cache"})
  throw Error()
  if (response.ok) {
    return await response.json()
  } else if (response.status === 404) {
    notFound()
  }
  throw Error(response.statusText)
}