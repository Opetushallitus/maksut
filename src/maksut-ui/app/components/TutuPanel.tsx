'use client'

import { Lasku } from "@/app/lib/types";
import styles from "@/app/page.module.css";
import Maksu from "@/app/components/Maksu";
import { Box } from "@mui/material";
import { useTranslations } from "@/app/i18n/useTranslations";

const TutuPanel = ({laskut}: {laskut: Array<Lasku>}) => {
  const kasittely = laskut.find((lasku) => lasku.order_id.endsWith('-1'))
  const paatos = laskut.find((lasku) => lasku.order_id.endsWith('-2'))
  const {t} = useTranslations();

  const stateText = () => {
    if (paatos) {
      if (paatos.status === 'paid') {
        return (
          <>
            <span>{t('tutuPanel.paatosMaksettu')}</span>
            <span>{t('maksutPanel.yhteiskäytto')}</span>
          </>
        )
      } else {
        return <span>{t('tutuPanel.paatosMaksamatta')}</span>
      }
    }
    if (kasittely) {
      if (kasittely.status === 'paid') {
        return (
          <>
            <span>{t('tutuPanel.kasittelyMaksettu1')}</span>
            <span>{t('tutuPanel.kasittelyMaksettu2')}</span>
            <span>{t('tutuPanel.kasittelyMaksettu3')}</span>
            <span>{t('maksutPanel.yhteiskäytto')}</span>
          </>
        )
      } else {
        return (
          <>
          <span>{t('tutuPanel.kasittelyMaksamatta1')}</span>
            <span>{t('tutuPanel.kasittelyMaksamatta2')}</span>
            <span>{t('tutuPanel.kasittelyMaksamatta3')}</span>
            <span>{t('tutuPanel.kasittelyMaksamatta4')}</span>
          </>
        )
      }
    }
  }

  return (
    <>
      <h2>{t('tutuPanel.title')}</h2>
      {stateText()}
      <Box className={styles.maksut}>
        {kasittely && <Maksu lasku={kasittely} title={t('maksu.käsittely')}/>}
        {paatos && <Maksu lasku={paatos} title={t('maksu.päätös')}/>}
      </Box>
    </>
  )
}

export default TutuPanel