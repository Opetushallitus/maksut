import { fetchLaskutBySecret } from "@/app/lib/data";
import { Lasku } from "@/app/lib/types";
import MaksutPanel from "@/app/components/MaksutPanel";
import { notFound, redirect } from "next/navigation";
import Header from "@/app/components/Header";
import { routing } from "@/i18n/routing";
import { getLocale } from "next-intl/server";
import ErrorPanel from "@/app/components/ErrorPanel";
import { Span } from "next/dist/server/lib/trace/tracer";
import ExpiredPanel from "@/app/components/ExpiredPanel";

export default async function Page({ searchParams }: {searchParams: Promise<{secret?: string}>}) {
  const { secret } = await searchParams
  const locale = await getLocale()

  if (!routing.locales.includes(locale as any)) {
    redirect(`/fi?secret=${secret}`)
  }

  if (!secret) {
    notFound()
  }

  const { laskut, contact } = await fetchLaskutBySecret(secret)
  const activeLasku = laskut.find((lasku) => lasku.secret === secret)

  if (!laskut.length || !activeLasku) {
    return (
      <ExpiredPanel contact={contact}/>
    )
  }


  return (
    <>
      <Header lasku={activeLasku}></Header>
      <MaksutPanel laskut={laskut} secret={secret}/>
    </>
  );
}
