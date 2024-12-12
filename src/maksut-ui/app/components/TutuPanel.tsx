'use client';

import { Lasku, PaymentState } from '@/app/lib/types';
import Maksu from '@/app/components/Maksu';
import { Box, useTheme } from '@mui/material';
import { useTranslations } from 'use-intl';
import Panel from '@/app/components/Panel';
import MaksaButton from '@/app/components/MaksaButton';
import TutuStateTracker from '@/app/components/TutuStateTracker';
import { OphTypography } from '@opetushallitus/oph-design-system';
import { styled } from '@mui/system';

const StyledBox = styled(Box)(({ theme }) => ({
  display: 'flex',
  [theme.breakpoints.down('sm')]: {
    flexDirection: 'column',
  },
  [theme.breakpoints.up('sm')]: {
    flexDirection: 'row',
  },
  gap: theme.spacing(4),
}));

const TutuPanel = ({
  laskut,
  activeLasku,
}: {
  laskut: Lasku[];
  activeLasku: Lasku;
}) => {
  const kasittely = laskut.find((lasku) => lasku.order_id.endsWith('-1'));
  const paatos = laskut.find((lasku) => lasku.order_id.endsWith('-2'));
  const t = useTranslations('TutuPanel');
  const tMaksut = useTranslations('MaksutPanel');
  const theme = useTheme();

  const state = (): PaymentState => {
    if (paatos) {
      if (paatos.status === 'paid') {
        return 'paatosmaksettu';
      } else {
        return 'paatosmaksamatta';
      }
    }
    if (kasittely?.status === 'paid') {
      return 'kasittelymaksettu';
    } else {
      return 'kasittelymaksamatta';
    }
  };

  const stateText = () => {
    switch (state()) {
      case 'kasittelymaksamatta':
        return (
          <Box
            style={{
              display: 'flex',
              flexDirection: 'column',
              textAlign: 'left',
              gap: theme.spacing(2),
            }}
          >
            <OphTypography>{t('käsittelyMaksamatta1')}</OphTypography>
            <OphTypography>{t('käsittelyMaksamatta2')}</OphTypography>
            <OphTypography>{t('käsittelyMaksamatta3')}</OphTypography>
            <OphTypography>{t('käsittelyMaksamatta4')}</OphTypography>
          </Box>
        );
      case 'kasittelymaksettu':
        return (
          <Box
            style={{
              display: 'flex',
              flexDirection: 'column',
              textAlign: 'left',
              gap: theme.spacing(2),
            }}
          >
            <OphTypography>{t('käsittelyMaksettu1')}</OphTypography>
            <OphTypography>{t('käsittelyMaksettu2')}</OphTypography>
            <OphTypography>{t('käsittelyMaksettu3')}</OphTypography>
            <OphTypography>{tMaksut('yhteiskäytto')}</OphTypography>
          </Box>
        );
      case 'paatosmaksamatta':
        return <OphTypography>{t('päätösMaksamatta')}</OphTypography>;
      case 'paatosmaksettu':
        return (
          <>
            <OphTypography>{t('päätösMaksettu')}</OphTypography>
            <OphTypography>{tMaksut('yhteiskäytto')}</OphTypography>
          </>
        );
    }
  };

  return (
    <Panel>
      <OphTypography variant={'h2'}>{t('title')}</OphTypography>
      <TutuStateTracker state={state()}></TutuStateTracker>
      {stateText()}
      <StyledBox>
        {kasittely && <Maksu lasku={kasittely} />}
        {paatos && <Maksu lasku={paatos} />}
      </StyledBox>
      <MaksaButton lasku={activeLasku}></MaksaButton>
    </Panel>
  );
};

export default TutuPanel;
