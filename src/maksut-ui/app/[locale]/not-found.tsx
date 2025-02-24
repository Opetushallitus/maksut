import { getTranslations } from 'next-intl/server';
import ErrorPanel from '@/app/components/ErrorPanel';
import { OphTypography } from '@opetushallitus/oph-design-system';

export default async function NotFound() {
  const t = await getTranslations('NotFound');

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
