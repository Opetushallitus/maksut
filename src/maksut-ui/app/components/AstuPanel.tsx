'use client';

import { Lasku, Locale } from '@/app/lib/types';
import Maksu from '@/app/components/Maksu';
import { useLocale, useTranslations } from 'use-intl';
import { translateLocalizedString } from '@/app/lib/utils';
import Panel from '@/app/components/Panel';
import MaksaButton from '@/app/components/MaksaButton';

const AstuPanel = ({ lasku }: { lasku: Lasku }) => {
  const t = useTranslations('AstuPanel');
  const locale = useLocale() as Locale;
  const tMaksut = useTranslations('MaksutPanel');

  const stateText = () => {
    if (lasku.status === 'paid') {
      return (
        <>
          <span>{t('päätösMaksettu')}</span>
          <span>{tMaksut('yhteiskäytto')}</span>
        </>
      );
    } else {
      return (
        <>
          <span>{t('päätösMaksamatta')}</span>
        </>
      );
    }
  };

  return (
    <Panel>
      <h2>
        {translateLocalizedString(
          lasku.metadata?.form_name,
          locale,
          'ASTU lomake',
        )}
      </h2>
      {stateText()}
      <Maksu lasku={lasku} />
      <MaksaButton lasku={lasku}></MaksaButton>
    </Panel>
  );
};

export default AstuPanel;
