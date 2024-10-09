import styles from "./page.module.css";
import { fetchLaskutBySecret } from "@/app/lib/data";
import { Lasku, Locale } from "@/app/lib/types";
import MaksutPanel from "@/app/components/MaksutPanel";
import { TopBar } from "@/app/components/TopBar";
import { notFound } from "next/navigation";
import Header from "@/app/components/Header";
import { getLocale } from "next-intl/server";

export default async function Page({ searchParams }: {searchParams: {secret?: string}}) {
  const { secret } = searchParams
  const locale = await getLocale() as Locale;

  if (!secret) {
    notFound()
  }

  const laskut: Array<Lasku> = await fetchLaskutBySecret(secret)

  if (!laskut.length) {
    notFound()
  }

  const firstLasku = laskut[0]

  return (
    <main className={styles.main}>
      <TopBar lang={locale}></TopBar>
      <Header lasku={firstLasku}></Header>
      <MaksutPanel laskut={laskut} secret={secret} locale={locale} />
    </main>
  );
}
