'use client'

import { Lasku } from "@/app/lib/types";
import styles from "@/app/[locale]/page.module.css";
import Maksu from "@/app/components/Maksu";
import { Box } from "@mui/material";
import { useTranslations } from "use-intl";
import { translateLocalizedString } from "@/app/lib/utils";

const AstuPanel = ({ lasku }: {lasku: Lasku}) => {
  const t = useTranslations('AstuPanel')

  return (
    <>
      <h2>{translateLocalizedString(lasku.metadata?.form_name)}</h2>
      <span>Hakemuksesi käsitelty jne. loremipsum</span>
      <Box className={styles.maksut}>
        <Maksu lasku={lasku} />
      </Box>
    </>)
}

export default AstuPanel