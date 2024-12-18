'use client';

import { TopBar } from '@/app/components/TopBar';
import ErrorPanel from '@/app/components/ErrorPanel';
import { OphNextJsThemeProvider } from '@opetushallitus/oph-design-system/next/theme';
import { CssBaseline } from '@mui/material';
import { CSSProperties } from 'react';
import { ophColors, OphTypography } from '@opetushallitus/oph-design-system';

export default function NotFound() {
  const bodyStyle: CSSProperties = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    backgroundColor: ophColors.grey50,
  };
  return (
    <html lang="en">
      <body style={bodyStyle}>
        <OphNextJsThemeProvider lang={'en'} variant={'opintopolku'}>
          <CssBaseline />
          <TopBar></TopBar>
          <ErrorPanel>
            <OphTypography variant={'h2'}>
              Maksun tietoja ei löydy
            </OphTypography>
            <OphTypography>Emme löytäneet maksusi tietoja.</OphTypography>
            <OphTypography>
              Ota yhteyttä Opetushallitukseen. Löydät palveluosoitteen
              saamastasi maksulinkkiviestistä.
            </OphTypography>
          </ErrorPanel>
        </OphNextJsThemeProvider>
      </body>
    </html>
  );
}
