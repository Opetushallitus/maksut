import { fetchLaskutBySecret } from "@/app/lib/data";
import { Lasku } from "@/app/lib/types";
import MaksutPanel from "@/app/components/MaksutPanel";
import { notFound, redirect } from "next/navigation";
import Header from "@/app/components/Header";
import { routing } from "@/i18n/routing";
import { getLocale } from "next-intl/server";

export default async function Page({ searchParams }: {searchParams: {secret?: string}}) {
  const { secret } = searchParams
  const locale = await getLocale()

  if (!routing.locales.includes(locale as any)) {
    redirect(`/fi?secret=${secret}`)
  }

  if (!secret) {
    notFound()
  }

  const laskut: Array<Lasku> = await fetchLaskutBySecret(secret)
  const activeLasku = laskut.find((lasku) => lasku.secret === secret)

  if (!laskut.length || !activeLasku) {
    notFound()
  }


  return (
    <main>
      <Header lasku={activeLasku}></Header>
      <MaksutPanel laskut={laskut} secret={secret}/>
    </main>
  );
}
