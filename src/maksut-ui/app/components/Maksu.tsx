'use client';

import { Lasku, PaymentStatus } from '@/app/lib/types';
import { useTranslations } from 'use-intl';
import { Box, useTheme } from '@mui/material';
import { ophColors, OphTypography } from '@opetushallitus/oph-design-system';
import { ReactNode } from 'react';
import { BigNumber } from 'bignumber.js';

const StatusRow = ({ status }: { status: PaymentStatus }) => {
  const t = useTranslations('Maksu');
  const theme = useTheme();

  const statusColors = {
    active: {
      backgroundColor: '#FAE6A8',
      dot: ophColors.orange2,
      text: ophColors.orange1,
    },
    overdue: {
      backgroundColor: '#EECFC5',
      dot: ophColors.orange3,
      text: ophColors.grey900,
    },
    paid: {
      backgroundColor: '#E2FAE4',
      dot: ophColors.green2,
      text: ophColors.green1,
    },
    invalidated: {
      backgroundColor: '#E2FAE4',
      dot: ophColors.green2,
      text: ophColors.green1,
    },
  };

  return (
    <>
      <OphTypography
        style={{
          textAlign: 'left',
          margin: theme.spacing(1.5, 2, 1.5, 0),
        }}
      >
        {t('tila')}
      </OphTypography>
      <Box
        style={{
          padding: theme.spacing(0.5, 1, 0.5, 1),
          fontWeight: '700',
          fontSize: '14px',
          whiteSpace: 'nowrap',
          width: 'min-content',
          margin: theme.spacing(1, 0, 1, 'auto'),
          backgroundColor: statusColors[status].backgroundColor,
          color: statusColors[status].text,
        }}
      >
        <Box
          style={{
            height: '10px',
            width: '10px',
            borderRadius: '50%',
            display: 'inline-block',
            marginRight: theme.spacing(1),
            backgroundColor: statusColors[status].dot,
          }}
        ></Box>
        {t(status)}
      </Box>
    </>
  );
};

const Separator = () => {
  return (
    <span
      style={{
        gridColumn: 'span 2',
        borderBottom: '1px solid #cecfd0',
      }}
    ></span>
  );
};

const MaksuRow = ({
  name,
  value,
  bold,
}: {
  name: string;
  value: ReactNode;
  bold?: boolean;
}) => {
  const theme = useTheme();

  return (
    <>
      <OphTypography
        style={{
          textAlign: 'left',
          margin: theme.spacing(1, 2, 1, 0),
        }}
      >
        {name}
      </OphTypography>
      <OphTypography
        style={{
          textAlign: 'right',
          margin: theme.spacing(1, 0, 1, 2),
          fontWeight: bold ? 'bold' : 'initial',
        }}
      >
        {value}
      </OphTypography>
    </>
  );
};

const Maksu = ({ lasku }: { lasku: Lasku }) => {
  const t = useTranslations('Maksu');
  const theme = useTheme();

  const parseDate = (date: string) => {
    const [year, month, day] = date.split('-');
    return `${day}.${month}.${year}`;
  };

  const title = () => {
    if (lasku.origin === 'tutu') {
      if (lasku.order_id.endsWith('-1')) {
        return t('käsittely');
      }
      return t('päätös');
    }
    return t('maksu');
  };

  const totalAmount = () => {
    if (lasku.vat) {
      return BigNumber(lasku.amount).plus(
        BigNumber(lasku.amount).multipliedBy(
          BigNumber(lasku.vat).dividedBy(BigNumber(100)),
        ),
      );
    }
    return BigNumber(lasku.amount);
  };

  return (
    <Box
      style={{
        backgroundColor: ophColors.grey50,
        textAlign: 'center',
      }}
    >
      <OphTypography
        variant={'h4'}
        component={'h3'}
        style={{ margin: theme.spacing(1) }}
      >
        {title()}
      </OphTypography>
      <Box
        style={{
          display: 'grid',
          justifyContent: 'space-between',
          gridTemplateColumns: '1fr 1fr',
          margin: theme.spacing(0, 4),
          paddingBottom: theme.spacing(1.5),
        }}
      >
        <StatusRow status={lasku.status} />
        <Separator />
        <MaksuRow
          name={t('summa')}
          value={`${totalAmount().toFixed(2)}€`}
          bold
        ></MaksuRow>
        <Separator />
        {lasku.status !== 'paid' ? (
          <MaksuRow
            name={t('eräpäivä')}
            value={parseDate(lasku.due_date)}
          ></MaksuRow>
        ) : (
          <MaksuRow
            name={t('maksupäivä')}
            value={parseDate(lasku.paid_at)}
          ></MaksuRow>
        )}
      </Box>
    </Box>
  );
};

export default Maksu;
