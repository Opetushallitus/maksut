'use client';

import { Lasku, Locale } from '@/app/lib/types';
import Maksu from '@/app/components/Maksu';
import { useLocale, useTranslations } from 'use-intl';
import Panel from '@/app/components/Panel';
import MaksaButton from '@/app/components/MaksaButton';
import { translateLocalizedString } from '@/app/lib/utils';
import { OphTypography } from '@opetushallitus/oph-design-system';

const KkHakemusmaksuPanel = ({ lasku }: { lasku: Lasku }) => {
  const t = useTranslations('KkHakemusmaksuPanel');
  const locale = useLocale() as Locale;
  const tMaksut = useTranslations('MaksutPanel');

  const aloituskausiText = (aloitusvuosi?: number, aloituskausi?: string) => {
    if (aloitusvuosi && aloituskausi) {
      return `${t(aloituskausi)} ${aloitusvuosi}`;
    }
    return null;
  };

  const aloituskausiHeader = (aloitusvuosi?: number, aloituskausi?: string) => {
    if (aloitusvuosi && aloituskausi) {
      return (
        <OphTypography variant={'h3'} style={{ margin: 0 }}>
          {t('aloituskausi')}: {aloituskausiText(aloitusvuosi, aloituskausi)}
        </OphTypography>
      );
    }
    return null;
  };

  const stateText = () => {
    if (lasku.status === 'paid') {
      return (
        <>
          <OphTypography>{t('maksettu')}</OphTypography>
          <OphTypography>
            {t('maksettu2')}{' '}
            {aloituskausiText(
              lasku.metadata?.alkamisvuosi,
              lasku.metadata?.alkamiskausi,
            )}
            . {t('maksettu3')}
          </OphTypography>
          <OphTypography>{t('maksettu4')}</OphTypography>
          <OphTypography>{tMaksut('yhteiskäytto')}</OphTypography>
        </>
      );
    } else if (lasku.status === 'overdue') {
      return (
        <>
          <OphTypography>{t('eraantynyt')}</OphTypography>
        </>
      );
    } else if (lasku.status === 'invalidated') {
      return (
        <>
          <OphTypography>
            {t('mitatoity')}{' '}
            {aloituskausiText(
              lasku.metadata?.alkamisvuosi,
              lasku.metadata?.alkamiskausi,
            )}
            . {t('mitatoity2')}
          </OphTypography>
          <OphTypography>{tMaksut('yhteiskäytto')}</OphTypography>
        </>
      );
    } else {
      return (
        <>
          <OphTypography>{t('maksamatta')}</OphTypography>
          <OphTypography>{t('maksamatta2')}</OphTypography>
        </>
      );
    }
  };

  return (
    <Panel>
      <OphTypography variant={'h2'} style={{ margin: 0 }}>
        {translateLocalizedString(
          lasku.metadata?.haku_name,
          locale,
          'Hakemusmaksu',
        )}
      </OphTypography>
      {aloituskausiHeader(
        lasku.metadata?.alkamisvuosi,
        lasku.metadata?.alkamiskausi,
      )}
      {stateText()}
      <Maksu lasku={lasku} />
      <MaksaButton lasku={lasku}></MaksaButton>
    </Panel>
  );
};

export default KkHakemusmaksuPanel;
