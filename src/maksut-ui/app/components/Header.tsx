import { Box } from '@mui/material';
import { Lasku } from '@/app/lib/types';
import { getTranslations } from 'next-intl/server';
import { OphTypography } from '@opetushallitus/oph-design-system';
import { ReactNode } from 'react';

const Header = async ({
  lasku,
}: {
  lasku: Lasku;
}): Promise<Awaited<ReactNode>> => {
  const t = await getTranslations('Header');

  return (
    <Box style={{ textAlign: 'center', margin: '20px' }}>
      <OphTypography
        variant={'h3'}
      >{`${lasku.first_name} ${lasku.last_name}`}</OphTypography>
      <OphTypography variant={'h1'}>{t('title')}</OphTypography>
    </Box>
  );
};

export default Header;
