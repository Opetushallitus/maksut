import { Lasku } from "@/app/lib/types";
import styles from "@/app/page.module.css";
import Maksu from "@/app/components/Maksu";
import { Box } from "@mui/material";

const AstuPanel = ({lasku}: {lasku: Lasku}) => {
  return (
    <>
      <h2>{lasku.reference}</h2>
      <span>Hakemuksesi käsitelty jne. loremipsum</span>
      <Box className={styles.maksut}>
        <Maksu lasku={lasku}/>
      </Box>
    </>)
}

export default AstuPanel