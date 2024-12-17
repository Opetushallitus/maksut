import ErrorPanel from '@/app/components/ErrorPanel';
import {
  OphButton,
  OphLink,
  OphTypography,
} from '@opetushallitus/oph-design-system';
import { getLocale, getTranslations } from 'next-intl/server';
import { fetchLaskuContact } from '@/app/lib/data';

export default async function ErrorPage({
  searchParams,
}: {
  searchParams: Promise<{ secret?: string }>;
}) {
  const { secret } = await searchParams;
  const locale = await getLocale();
  const t = await getTranslations({
    locale: locale,
    namespace: 'PaymentError',
  });

  const { contact } = secret
    ? await fetchLaskuContact(secret)
    : { contact: 'recognition@oph.fi' };

  return (
    <ErrorPanel>
      <OphTypography variant={'h2'}>{t('header')}</OphTypography>
      <OphTypography>{t('body1')}</OphTypography>
      <OphTypography>{t('body2')}</OphTypography>
      <OphTypography>
        {t('body3')}
        <OphLink href={`mailto:${contact}`}>{contact}</OphLink>.
      </OphTypography>
      <OphButton variant={'contained'} href={`/${locale}?secret=${secret}`}>
        {t('returnButton')}
      </OphButton>
    </ErrorPanel>
  );
}
