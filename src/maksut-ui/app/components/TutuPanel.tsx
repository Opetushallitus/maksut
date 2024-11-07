'use client'

import { Lasku, PaymentState } from "@/app/lib/types";
import Maksu from "@/app/components/Maksu";
import { Box, useTheme } from "@mui/material";
import { useTranslations } from "use-intl";
import Panel from "@/app/components/Panel";
import MaksaButton from "@/app/components/MaksaButton";
import TutuStateTracker from "@/app/components/TutuStateTracker";

const TutuPanel = ({laskut, activeLasku}: {laskut: Array<Lasku>, activeLasku: Lasku}) => {
  const kasittely = laskut.find((lasku) => lasku.order_id.endsWith('-1'))
  const paatos = laskut.find((lasku) => lasku.order_id.endsWith('-2'))
  const t = useTranslations('TutuPanel')
  const tMaksut = useTranslations('MaksutPanel')
  const theme = useTheme()

  const state = (): PaymentState => {
    if (paatos) {
      if (paatos.status === 'paid') {
        return 'paatosmaksettu'
      } else {
        return 'paatosmaksamatta'
      }
    }
    if (kasittely?.status === 'paid') {
      return 'kasittelymaksettu'
    } else {
      return 'kasittelymaksamatta'
    }
  }

  const stateText = () => {
    switch (state()) {
      case 'kasittelymaksamatta':
        return (
          <>
            <span>{t('käsittelyMaksamatta1')}</span>
            <span>{t('käsittelyMaksamatta2')}</span>
            <span>{t('käsittelyMaksamatta3')}</span>
            <span>{t('käsittelyMaksamatta4')}</span>
          </>
        )
      case 'kasittelymaksettu':
        return (
          <>
            <span>{t('käsittelyMaksettu1')}</span>
            <span>{t('käsittelyMaksettu2')}</span>
            <span>{t('käsittelyMaksettu3')}</span>
            <span>{tMaksut('yhteiskäytto')}</span>
          </>
        )
      case 'paatosmaksamatta':
        return <span>{t('päätösMaksamatta')}</span>
      case 'paatosmaksettu':
        return (
          <>
            <span>{t('päätösMaksettu')}</span>
            <span>{tMaksut('yhteiskäytto')}</span>
          </>
        )
    }
  }

  return (
    <Panel>
      <h2>{t('title')}</h2>
      <TutuStateTracker state={state()}></TutuStateTracker>
      <Box style={{display: 'flex', flexDirection: 'column', textAlign: 'left', gap: theme.spacing(2)}}>
        {stateText()}
      </Box>
      <Box style={{
        display: 'flex',
        flexDirection: 'row',
        gap: theme.spacing(4),
      }}>
        {kasittely && <Maksu lasku={kasittely}/>}
        {paatos && <Maksu lasku={paatos}/>}
      </Box>
      <MaksaButton lasku={activeLasku}></MaksaButton>
    </Panel>
  )
}

export default TutuPanel