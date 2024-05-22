import styles from "./page.module.css";
import { Button } from '@opetushallitus/oph-design-system'
import { fetchLaskutBySecret } from "@/app/lib/data";
import { match } from "assert";
import { Origin } from "@/app/lib/types";
import TutuPanel from "@/app/components/TutuPanel";
import AstuPanel from "@/app/components/AstuPanel";

export default async function Page({ searchParams }: {searchParams: {secret?: string, locale?: string}}) {
  const { secret, locale} = searchParams
  const laskut = await fetchLaskutBySecret(secret)
  const firstLasku = laskut[0]
  const origin: Origin = firstLasku.origin

  const panel = () => {
    switch (origin) {
      case 'tutu':
        return <TutuPanel laskut={laskut}/>
      case 'astu':
        return <AstuPanel lasku={firstLasku}/>
    }
  }
  return (
    <main className={styles.main}>
      <div className={styles.header}>
        <h3>{`${firstLasku.last_name} ${firstLasku.first_name}`}</h3>
        <h1>Maksutapahtumat</h1>
        {panel()}
      </div>

    </main>
  );
}
