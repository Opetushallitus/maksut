'use client';

import ErrorPanel from '@/app/components/ErrorPanel';
import { useTranslations } from 'use-intl';
import { OphTypography } from '@opetushallitus/oph-design-system';

export default function ErrorPage() {
  const t = useTranslations('PaymentError');

  return (
    <ErrorPanel>
      <OphTypography variant={'h2'}>{t('header')}</OphTypography>
    </ErrorPanel>
  );
}
