import styles from "./page.module.css";
import { fetchLaskutBySecret } from "@/app/lib/data";
import { Lasku } from "@/app/lib/types";
import MaksutPanel from "@/app/components/MaksutPanel";

export default async function Page({ searchParams }: {searchParams: {secret?: string, locale?: string}}) {
  const { secret, locale} = searchParams
  const laskut: Array<Lasku> = await fetchLaskutBySecret(secret)
  const firstLasku = laskut[0]

  return (
    <main className={styles.main}>
      <div className={styles.header}>
        <h3>{`${firstLasku.last_name} ${firstLasku.first_name}`}</h3>
        <h1>Maksutapahtumat</h1>
      </div>
      <MaksutPanel laskut={laskut} secret={secret} locale={locale}/>
    </main>
  );
}
