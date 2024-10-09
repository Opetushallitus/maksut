'use client'

import { Lasku } from "@/app/lib/types";
import styles from "@/app/[locale]/page.module.css";
import Maksu from "@/app/components/Maksu";
import { Box } from "@mui/material";
import { useTranslations } from "use-intl";

const TutuPanel = ({laskut}: {laskut: Array<Lasku>}) => {
  const kasittely = laskut.find((lasku) => lasku.order_id.endsWith('-1'))
  const paatos = laskut.find((lasku) => lasku.order_id.endsWith('-2'))
  const t = useTranslations('TutuPanel')
  const tMaksut = useTranslations('MaksutPanel')

  const stateText = () => {
    if (paatos) {
      if (paatos.status === 'paid') {
        return (
          <>
            <span>{t('päätösMaksettu')}</span>
            <span>{t('yhteiskäytto')}</span>
          </>
        )
      } else {
        return <span>{t('päätösMaksamatta')}</span>
      }
    }
    if (kasittely) {
      if (kasittely.status === 'paid') {
        return (
          <>
            <span>{t('käsittelyMaksettu1')}</span>
            <span>{t('käsittelyMaksettu2')}</span>
            <span>{t('käsittelyMaksettu3')}</span>
            <span>{tMaksut('yhteiskäytto')}</span>
          </>
        )
      } else {
        return (
          <>
            <span>{t('käsittelyMaksamatta1')}</span>
            <span>{t('käsittelyMaksamatta2')}</span>
            <span>{t('käsittelyMaksamatta3')}</span>
            <span>{t('käsittelyMaksamatta4')}</span>
          </>
        )
      }
    }
  }

  return (
    <>
      <h2>{t('title')}</h2>
      {stateText()}
      <Box className={styles.maksut}>
        {kasittely && <Maksu lasku={kasittely}/>}
        {paatos && <Maksu lasku={paatos}/>}
      </Box>
    </>
  )
}

export default TutuPanel