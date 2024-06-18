import { Lasku } from "@/app/lib/types";
import styles from "@/app/page.module.css";
import Maksu from "@/app/components/Maksu";
import { Box, Button } from "@mui/material";

const TutuPanel = ({laskut}: {laskut: Array<Lasku>}) => {
  const kasittely = laskut.find((lasku) => lasku.order_id.endsWith('-1'))
  const paatos = laskut.find((lasku) => lasku.order_id.endsWith('-2'))


  return (
    <>
      <h2>Tutu lasku title</h2>
      <span>Hakemuksesi käsitelty jne. loremipsum</span>
      <Box className={styles.maksut}>
        {kasittely && <Maksu lasku={kasittely}/>}
        {paatos && <Maksu lasku={paatos}/>}
      </Box>
    </>
  )
}

export default TutuPanel