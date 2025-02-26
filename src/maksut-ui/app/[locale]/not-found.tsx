'use client';

import ErrorPanel from '@/app/components/ErrorPanel';
import { OphTypography } from '@opetushallitus/oph-design-system';
import { useTranslations } from 'use-intl';

export default function NotFound() {
  const t = useTranslations('NotFound');

  return (
    <ErrorPanel>
      <OphTypography variant={'h2'} component={'h1'}>
        {t('header')}
      </OphTypography>
      <OphTypography>{t('body1')}</OphTypography>
      <OphTypography>{t('body2')}</OphTypography>
    </ErrorPanel>
  );
}
