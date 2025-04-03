'use client';

import { Box } from '@mui/material';
import { Lasku } from '@/app/lib/types';
import { OphTypography } from '@opetushallitus/oph-design-system';
import { useTranslations } from 'use-intl';

const Header = ({ lasku }: { lasku: Lasku }) => {
  const t = useTranslations('Header');

  return (
    <Box style={{ textAlign: 'center', marginTop: '20px' }}>
      <OphTypography
        variant={'h3'}
      >{`${lasku.first_name} ${lasku.last_name}`}</OphTypography>
      <OphTypography variant={'h1'}>{t('title')}</OphTypography>
    </Box>
  );
};

export default Header;
