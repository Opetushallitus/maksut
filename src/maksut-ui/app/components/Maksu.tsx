'use client'

import { Lasku, PaymentStatus } from '@/app/lib/types'
import { useTranslations } from 'use-intl'
import { Box, useTheme } from '@mui/material'
import { colors } from '@opetushallitus/oph-design-system'
import { ReactNode } from 'react'

const StatusRow = ({ status }: { status: PaymentStatus }) => {
  const t = useTranslations('Maksu')
  const theme = useTheme()

  const statusColors = {
      'active': {
        backgroundColor: '#f9e39f',
        dot: '#de9327',
        text: '#612d00',
      },
      'overdue': {
        backgroundColor: '#ff543b',
        dot: '#c72828',
        text: '#f8f8f8',
      },
      'paid': {
        backgroundColor: '#e2fae4',
        dot: '#61a33b',
        text: '#237a00',
      }
    }

  return (
    <>
      <span style={{
        textAlign: 'left',
        margin: theme.spacing(1.5, 2, 1.5, 0),
      }}>{t('tila')}</span>
      <Box style={{
        padding: theme.spacing(0.5, 1, 0.5, 1),
        fontWeight: '700',
        fontSize:   '14px',
        whiteSpace: 'nowrap',
        width: 'min-content',
        margin: theme.spacing(1, 0, 1, 2),
        backgroundColor: statusColors[status].backgroundColor,
        color: statusColors[status].text,
      }}>
        <Box style={{
          height: '10px',
          width: '10px',
          borderRadius: '50%',
          display: 'inline-block',
          marginRight: '5px',
          backgroundColor: statusColors[status].dot,
        }}></Box>
        {t(status)}
      </Box>
    </>
  )
}

const Separator = () => {
  return <span style={{
    gridColumn: 'span 2',
    borderBottom: '1px solid #cecfd0',
  }}></span>
}

const MaksuRow = ({name, value, bold}: { name: string, value: ReactNode, bold?: boolean}) => {
  const theme = useTheme()

  return (
    <>
      <span style={{
        textAlign: 'left',
        margin: theme.spacing(1, 2, 1, 0),
      }}>{name}</span>
      <span style={{
        textAlign: 'right',
        margin: theme.spacing(1, 0, 1, 2),
        fontWeight: bold ? 'bold' : 'initial',
      }}>{value}</span>
    </>
  )
}

const Maksu = ({lasku}: {lasku: Lasku}) => {
  const t = useTranslations('Maksu')
  const theme = useTheme()

  const parseDate = (date: string) => {
    const [year, month, day] = date.split('-')
    return `${day}.${month}.${year}`
  }

  const title = () => {
    if (lasku.origin === 'astu') {
      return t('maksu')
    }
    if (lasku.origin === 'tutu') {
      if (lasku.order_id.endsWith('-1')) {
        return t('käsittely')
      }
      return t('päätös')
    }
    return t('käsittely')
  }

  return (
    <Box style={{
      backgroundColor: colors.grey50,
      textAlign: 'center',
      margin: theme.spacing(2),
    }}>
      <h4 style={{margin: theme.spacing(1)}}>{title()}</h4>
      <Box style={{
        display: 'grid',
        justifyContent: 'space-between',
        gridTemplateColumns: '1fr 1fr',
        margin: theme.spacing(0, 4),
        paddingBottom: theme.spacing(1.5),
      }}>
        <StatusRow status={lasku.status}/>
        <Separator />
        <MaksuRow name={t('summa')} value={`${lasku.amount}€`} bold></MaksuRow>
        <Separator />
        {lasku.status !== 'paid' ?
          <MaksuRow name={t('eräpäivä')} value={parseDate(lasku.due_date)}></MaksuRow> :
          <MaksuRow name={t('maksupäivä')} value={parseDate(lasku.paid_at)}></MaksuRow>
        }
      </Box>
    </Box>
  )
}

export default Maksu