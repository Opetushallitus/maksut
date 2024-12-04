'use client';

import ErrorPanel from '@/app/components/ErrorPanel';
import { useTranslations } from 'use-intl';

export default function ErrorPage() {
  const t = useTranslations('PaymentError');

  return (
    <ErrorPanel>
      <h2>{t('header')}</h2>
    </ErrorPanel>
  );
}
