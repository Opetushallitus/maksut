'use client';

import ErrorPanel from '@/app/components/ErrorPanel';
import { useTranslations } from 'use-intl';

export default function Error() {
  const t = useTranslations('Error');
  return (
    <ErrorPanel>
      <h2>{t('header')}</h2>
    </ErrorPanel>
  );
}
