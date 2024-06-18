import styles from "./page.module.css";
import { fetchLaskutBySecret } from "@/app/lib/data";
import { Lasku, Locale } from "@/app/lib/types";
import MaksutPanel from "@/app/components/MaksutPanel";
import { TopBar } from "@/app/components/TopBar";
import { notFound } from "next/navigation";
import { Box } from "@mui/material";
import { createTranslation } from "@/app/i18n/server";

export default async function Page({ searchParams }: {searchParams: {secret?: string, locale?: Locale}}) {
  const { secret, locale} = searchParams

  if (!secret) {
    notFound()
  }

  const laskut: Array<Lasku> = await fetchLaskutBySecret(secret)
  const firstLasku = laskut[0]
  const lang: Locale = locale ? locale : 'fi'

  const { t } = await createTranslation(lang);

  return (
    <main className={styles.main}>
      <TopBar lang={lang}></TopBar>
      <Box style={{textAlign: 'center'}}>
        <h3>{`${firstLasku.last_name} ${firstLasku.first_name}`}</h3>
        <h1>Maksutapahtumat</h1>
      </Box>
      <MaksutPanel laskut={laskut} secret={secret} locale={lang}/>
    </main>
  );
}
