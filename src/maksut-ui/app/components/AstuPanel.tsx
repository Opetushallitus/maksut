'use client'

import { Lasku } from "@/app/lib/types";
import styles from "@/app/page.module.css";
import Maksu from "@/app/components/Maksu";
import { Box } from "@mui/material";
import { useTranslations } from "@/app/i18n/useTranslations";

const AstuPanel = ({ lasku }: {lasku: Lasku}) => {
  const {t, translateEntity} = useTranslations();

  return (
    <>
      <h2>{translateEntity(lasku.metadata?.form_name)}</h2>
      <span>Hakemuksesi käsitelty jne. loremipsum</span>
      <Box className={styles.maksut}>
        <Maksu lasku={lasku} title={t('maksu.päätös')}/>
      </Box>
    </>)
}

export default AstuPanel