'use client';

import { Lasku } from '@/app/lib/types';
import Maksu from '@/app/components/Maksu';
import { useTranslations } from 'use-intl';
import Panel from '@/app/components/Panel';
import MaksaButton from '@/app/components/MaksaButton';

const KkHakemusmaksuPanel = ({ lasku }: { lasku: Lasku }) => {
  const t = useTranslations('KkHakemusmaksuPanel');
  const tMaksut = useTranslations('MaksutPanel');

  const aloituskausiHeader = (aloitusvuosi?: number, aloituskausi?: string) => {
    if (aloitusvuosi && aloituskausi) {
      return (
          <h3 style={{margin: 0}}>{t('aloituskausi')}: {t(aloituskausi)} {aloitusvuosi}</h3>
      )
    }
    return null;
  }

  const stateText = () => {
    if (lasku.status === 'paid') {
      return (
        <>
          <span>{t('maksettu')}</span>
          <span>{tMaksut('yhteiskäytto')}</span>
        </>
      )
    }
    else if (lasku.status === 'overdue') {
      return (
        <>
          <span>{t('eraantynyt')}</span>
        </>
      )
    }
    else {
      return (
        <>
          <span>{t('maksamatta')}</span>
        </>
      );
    }
  };

  return (
    <Panel>
      <h2 style={{margin: 0}}>{translateLocalizedString(lasku.metadata?.haku_name, locale, "Hakemusmaksu")}</h2>
      {aloituskausiHeader(lasku.metadata?.alkamisvuosi, lasku.metadata?.alkamiskausi)}
      {stateText()}
      <Maksu lasku={lasku} />
      <MaksaButton lasku={lasku}></MaksaButton>
    </Panel>
  );
};

export default KkHakemusmaksuPanel;
