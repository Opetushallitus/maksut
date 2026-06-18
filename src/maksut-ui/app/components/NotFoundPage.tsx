'use client';

import { useTranslations } from 'use-intl';
import { OphTypography } from '@opetushallitus/oph-design-system';
import ErrorPanel from '@/app/components/ErrorPanel';

export default function NotFoundPage() {
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
