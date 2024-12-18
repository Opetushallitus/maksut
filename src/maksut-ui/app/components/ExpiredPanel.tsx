'use client';

import ErrorPanel from '@/app/components/ErrorPanel';
import { useTranslations } from 'use-intl';
import { OphLink, OphTypography } from '@opetushallitus/oph-design-system';

const ExpiredPanel = ({ contact }: { contact: string | undefined }) => {
  const t = useTranslations('ExpiredPanel');

  return (
    <ErrorPanel>
      <OphTypography variant={'h2'}>{t('header')}</OphTypography>
      <OphTypography>{t('body1')}</OphTypography>
      <OphTypography>
        {`${t('body2')} `}
        <OphLink href={`mailto:${contact}`}>{contact}</OphLink>.
      </OphTypography>
    </ErrorPanel>
  );
};

export default ExpiredPanel;
