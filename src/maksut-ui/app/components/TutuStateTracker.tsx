'use client';

import { Box, useTheme } from '@mui/material';
import DoneIcon from '@mui/icons-material/Done';
import { useTranslations } from 'use-intl';
import { CSSProperties, ReactNode } from 'react';
import { ophColors, OphTypography } from '@opetushallitus/oph-design-system';

type PaymentState =
  | 'kasittelymaksamatta'
  | 'kasittelymaksettu'
  | 'paatosmaksamatta'
  | 'paatosmaksettu';

const CircleIcon = ({
  index,
  active,
  done,
}: {
  index: number;
  active: boolean;
  done: boolean;
}) => {
  const theme = useTheme();

  return (
    <Box
      style={{
        backgroundColor: active ? ophColors.green2 : ophColors.white,
        border: `2px solid ${active ? ophColors.green2 : ophColors.grey400}`,
        borderRadius: '50%',
        fontWeight: '600',
        height: theme.spacing(5),
        width: theme.spacing(5),
        color: active ? ophColors.white : ophColors.black,
      }}
    >
      <Box style={{ marginTop: theme.spacing(0.75) }}>
        {done ? <DoneIcon></DoneIcon> : <OphTypography>{index}</OphTypography>}
      </Box>
    </Box>
  );
};

const State = ({ children }: { children: ReactNode }) => {
  return (
    <Box
      style={{
        display: 'flex',
        alignItems: 'center',
        flexDirection: 'column',
        width: '200px',
        gap: '10px',
      }}
    >
      {children}
    </Box>
  );
};

const activeHeaderStyle: CSSProperties = {
  width: 'min-content',
  whiteSpace: 'nowrap',
  margin: 'auto',
  fontWeight: 400,
  color: ophColors.green2,
};

const passiveHeaderStyle: CSSProperties = {
  ...activeHeaderStyle,
  color: ophColors.grey800,
};

const KasittelyState = ({ state }: { state: PaymentState }) => {
  const t = useTranslations('TutuStateTracker');

  const header = () => {
    switch (state) {
      case 'kasittelymaksamatta':
        return (
          <OphTypography variant={'h4'} style={activeHeaderStyle}>
            {t('käsittelymaksu')}
          </OphTypography>
        );
      case 'kasittelymaksettu':
        return (
          <OphTypography variant={'h4'} style={passiveHeaderStyle}>
            {t('käsittelymaksu')}
          </OphTypography>
        );
      case 'paatosmaksamatta':
        return (
          <OphTypography variant={'h4'} style={activeHeaderStyle}>
            {t('käsittely')}
          </OphTypography>
        );
      case 'paatosmaksettu':
        return (
          <OphTypography variant={'h4'} style={passiveHeaderStyle}>
            {t('käsittely')}
          </OphTypography>
        );
    }
  };

  return (
    <State>
      <CircleIcon
        index={1}
        active={true}
        done={state !== 'kasittelymaksamatta'}
      ></CircleIcon>
      {header()}
    </State>
  );
};

const PaatosState = ({ state }: { state: PaymentState }) => {
  const t = useTranslations('TutuStateTracker');

  const header = () => {
    switch (state) {
      case 'kasittelymaksamatta':
        return (
          <OphTypography variant={'h4'} style={passiveHeaderStyle}>
            {t('käsittely')}
          </OphTypography>
        );
      case 'kasittelymaksettu':
        return (
          <OphTypography variant={'h4'} style={activeHeaderStyle}>
            {t('käsittely')}
          </OphTypography>
        );
      case 'paatosmaksamatta':
        return (
          <OphTypography variant={'h4'} style={passiveHeaderStyle}>
            {t('päätösmaksu')}
          </OphTypography>
        );
      case 'paatosmaksettu':
        return (
          <OphTypography variant={'h4'} style={activeHeaderStyle}>
            {t('päätösmaksu')}
          </OphTypography>
        );
    }
  };

  return (
    <State>
      <CircleIcon
        index={2}
        active={state === 'kasittelymaksettu' || state === 'paatosmaksettu'}
        done={state === 'paatosmaksettu'}
      ></CircleIcon>
      {header()}
    </State>
  );
};

const TutuStateTracker = ({ state }: { state: PaymentState }) => {
  const theme = useTheme();

  return (
    <Box
      style={{
        display: 'flex',
        flexDirection: 'row',
        marginBottom: theme.spacing(2),
      }}
    >
      <KasittelyState state={state}></KasittelyState>
      <PaatosState state={state}></PaatosState>
    </Box>
  );
};

export default TutuStateTracker;
