'use client'

import { Lasku } from "@/app/lib/types";
import Maksu from "@/app/components/Maksu";
import { Box, useTheme } from "@mui/material";
import { useTranslations } from "use-intl";
import { translateLocalizedString } from "@/app/lib/utils";

const AstuPanel = ({ lasku }: {lasku: Lasku}) => {
  const t = useTranslations('AstuPanel')
  const tMaksut = useTranslations('MaksutPanel')
  const theme = useTheme()

  const stateText = () => {
    if (lasku.status === 'paid') {
      return (
        <>
          <span>{t('päätösMaksettu')}</span>
          <span>{tMaksut('yhteiskäytto')}</span>
        </>
      )
    } else {
      return (
        <>
          <span>{t('päätösMaksamatta')}</span>
        </>
      )
    }
  }


  return (
    <>
      <h2>{translateLocalizedString(lasku.metadata?.form_name)}</h2>
      <Box style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        gap: theme.spacing(2),
        margin: theme.spacing(2, 4),
      }}>
        {stateText()}
      </Box>
      <Box style={{
        display: 'flex',
        flexDirection: 'row',
      }}>
        <Maksu lasku={lasku} />
      </Box>
    </>)
}

export default AstuPanel