'use client';

import ErrorPanel from '@/app/components/ErrorPanel';
import { useTranslations } from 'use-intl';
import { OphTypography } from '@opetushallitus/oph-design-system';

export default function Error() {
  const t = useTranslations('Error');
  return (
    <ErrorPanel>
      <OphTypography variant={'h2'} component={'h1'}>
        {t('header')}
      </OphTypography>
    </ErrorPanel>
  );
}
