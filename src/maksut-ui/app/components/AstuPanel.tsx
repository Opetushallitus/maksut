'use client';

import { Lasku, Locale } from '@/app/lib/types';
import Maksu from '@/app/components/Maksu';
import { useLocale, useTranslations } from 'use-intl';
import { translateLocalizedString } from '@/app/lib/utils';
import Panel from '@/app/components/Panel';
import MaksaButton from '@/app/components/MaksaButton';
import { OphTypography } from '@opetushallitus/oph-design-system';

const AstuPanel = ({ lasku }: { lasku: Lasku }) => {
  const t = useTranslations('AstuPanel');
  const locale = useLocale() as Locale;
  const tMaksut = useTranslations('MaksutPanel');

  const stateText = () => {
    if (lasku.status === 'paid') {
      return (
        <>
          <OphTypography>{t('päätösMaksettu')}</OphTypography>
          <OphTypography>{tMaksut('yhteiskäytto')}</OphTypography>
        </>
      );
    } else {
      return (
        <>
          <OphTypography>{t('päätösMaksamatta')}</OphTypography>
        </>
      );
    }
  };

  return (
    <Panel>
      <OphTypography variant={'h2'}>
        {translateLocalizedString(
          lasku.metadata?.form_name,
          locale,
          'ASTU lomake',
        )}
      </OphTypography>
      {stateText()}
      <Maksu lasku={lasku} />
      <MaksaButton lasku={lasku}></MaksaButton>
    </Panel>
  );
};

export default AstuPanel;
