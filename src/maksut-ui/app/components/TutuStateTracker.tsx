'use client'

import { Box, useTheme } from "@mui/material";
import DoneIcon from '@mui/icons-material/Done'
import { useTranslations } from "use-intl";
import { CSSProperties, ReactNode } from "react";

type PaymentState = 'kasittelymaksamatta' | 'kasittelymaksettu' | 'paatosmaksamatta' | 'paatosmaksettu'

const CircleIcon = ({index, active, done}: {index: number, active: boolean, done: boolean}) => {
  const theme = useTheme()

  return (
    <Box style={{
      backgroundColor : active ? '#3A7A10' : '#ffffff',
      border: `2px solid ${active ? '#3A7A10' : '#aeaeae'}`,
      borderRadius: "50%",
      fontWeight: "600",
      height: theme.spacing(5),
      width: theme.spacing(5),
      color: active ? '#ffffff' : '#101010',
    }}>
      <Box style={{marginTop: theme.spacing(0.75)}}>
        {done ?
          <DoneIcon></DoneIcon> :
          <span>{index}</span>
        }
      </Box>
    </Box>
  )
}

const State = ({children}: {children: ReactNode}) => {
  return (
    <Box style={{
      display: 'flex',
      alignItems: 'center',
      flexDirection: 'column',
      width: '200px',
      gap: '10px',
    }}>
      {children}
    </Box>
  )
}

const activeHeaderStyle: CSSProperties = {
  width: 'min-content',
  whiteSpace: 'nowrap',
  margin: 'auto',
  fontWeight: 400,
  color: '#3A7A10',
}

const passiveHeaderStyle: CSSProperties = {
  ...activeHeaderStyle,
  color: '#353535'
}

const KasittelyState = ({state}: {state: PaymentState}) => {
  const t = useTranslations('TutuStateTracker')

  const header = () => {
    switch (state) {
      case 'kasittelymaksamatta':
        return <h4 style={activeHeaderStyle}>{t('käsittelymaksu')}</h4>
      case 'kasittelymaksettu':
        return <h4 style={passiveHeaderStyle}>{t('käsittelymaksu')}</h4>
      case 'paatosmaksamatta':
        return <h4 style={activeHeaderStyle}>{t('käsittely')}</h4>
      case 'paatosmaksettu':
        return <h4 style={passiveHeaderStyle}>{t('käsittely')}</h4>
    }
  }

  return (
    <State>
      <CircleIcon index={1} active={true} done={state !== 'kasittelymaksamatta'}></CircleIcon>
      {header()}
    </State>
  )
}

const PaatosState = ({state}: {state: PaymentState}) => {
  const t = useTranslations('TutuStateTracker')

  const header = () => {
    switch (state) {
      case 'kasittelymaksamatta':
        return <h4 style={passiveHeaderStyle}>{t('käsittely')}</h4>
      case 'kasittelymaksettu':
        return <h4 style={activeHeaderStyle}>{t('käsittely')}</h4>
      case 'paatosmaksamatta':
        return <h4 style={passiveHeaderStyle}>{t('päätösmaksu')}</h4>
      case 'paatosmaksettu':
        return <h4 style={activeHeaderStyle}>{t('päätösmaksu')}</h4>
    }
  }

  return (
    <State>
      <CircleIcon
        index={2}
        active={state === 'kasittelymaksettu' || state === 'paatosmaksettu'}
        done={state === 'paatosmaksettu'}>
      </CircleIcon>
      {header()}
    </State>
  )
}

const TutuStateTracker = ({state}: {state: PaymentState}) => {
  const theme = useTheme()

  return (
    <Box style={{
      display: 'flex',
      flexDirection: 'row',
      marginBottom: theme.spacing(2),
    }}>
      <KasittelyState state={state}></KasittelyState>
      <PaatosState state={state}></PaatosState>
    </Box>
  )
}

export default TutuStateTracker