'use client';

import ErrorPanel from '@/app/components/ErrorPanel';
import {
  OphButton,
  OphLink,
  OphTypography,
} from '@opetushallitus/oph-design-system';
import { useLocale, useTranslations } from 'use-intl';

export default function ErrorPageContent({
  contact,
  secret,
}: {
  contact: string;
  secret: string;
}) {
  const locale = useLocale();
  const t = useTranslations('PaymentError');

  return (
    <ErrorPanel>
      <OphTypography variant={'h2'} component={'h1'}>
        {t('header')}
      </OphTypography>
      <OphTypography>{t('body1')}</OphTypography>
      <OphTypography>{t('body2')}</OphTypography>
      <OphTypography>
        {`${t('body3')} `}
        <OphLink href={`mailto:${contact}`}>{contact}</OphLink>.
      </OphTypography>
      <OphButton variant={'contained'} href={`/${locale}?secret=${secret}`}>
        {t('returnButton')}
      </OphButton>
    </ErrorPanel>
  );
}
