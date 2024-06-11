import styles from "../page.module.css";
import { Lasku, Origin } from "@/app/lib/types";
import TutuPanel from "@/app/components/TutuPanel";
import AstuPanel from "@/app/components/AstuPanel";
import { Box } from "@mui/material";
import { Button } from "@opetushallitus/oph-design-system"
import { backendUrl } from "@/app/lib/configurations";

export default async function MaksutPanel({ laskut, secret, locale }: {laskut: Array<Lasku>, secret?: string, locale?: string}) {
  const firstLasku = laskut[0]
  const origin: Origin = firstLasku.origin
  const orderId = laskut.find((lasku) => lasku.secret === secret)?.order_id

  const panel = () => {
    switch (origin) {
      case 'tutu':
        return <TutuPanel laskut={laskut}/>
      case 'astu':
        return <AstuPanel lasku={firstLasku}/>
    }
  }
  return (
    <Box className={styles.panel}>
      {panel()}
      <Button variant={'contained'} href={`${backendUrl}/lasku/${orderId}/maksa?secret=${secret}&locale=${locale}`}>Maksa</Button>
    </Box>
  );
}
