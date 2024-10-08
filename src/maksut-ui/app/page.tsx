import styles from "./page.module.css";
import { fetchLaskutBySecret } from "@/app/lib/data";
import { Lasku, Locale } from "@/app/lib/types";
import MaksutPanel from "@/app/components/MaksutPanel";
import { TopBar } from "@/app/components/TopBar";
import { notFound } from "next/navigation";
import Header from "@/app/components/Header";
import LocalizationsProvider from "@/app/i18n/localizationsProvider";

export default async function Page({ searchParams }: {searchParams: {secret?: string, locale?: Locale}}) {
  const { secret, locale} = searchParams
  const lang: Locale = locale ? locale : 'fi'

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
      <TopBar lang={lang}></TopBar>
      <LocalizationsProvider>
        <Header lasku={firstLasku}></Header>
        <MaksutPanel laskut={laskut} secret={secret} locale={lang}/>
      </LocalizationsProvider>
    </main>
  );
}
