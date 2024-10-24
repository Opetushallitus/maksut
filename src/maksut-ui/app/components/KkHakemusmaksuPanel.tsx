'use client'

import { Lasku, Locale } from "@/app/lib/types";
import Maksu from "@/app/components/Maksu";
import { useLocale, useTranslations } from "use-intl";
import { translateLocalizedString } from "@/app/lib/utils";
import Panel from "@/app/components/Panel";
import MaksaButton from "@/app/components/MaksaButton";

const KkHakemusmaksuPanel = ({ lasku }: {lasku: Lasku}) => {
  const t = useTranslations('KkHakemusmaksuPanel')
  const locale = useLocale() as Locale
  const tMaksut = useTranslations('MaksutPanel')

  const stateText = () => {
    if (lasku.status === 'paid') {
      return (
        <>
          <span>{t('maksettu')}</span>
          <span>{tMaksut('yhteiskäytto')}</span>
        </>
      )
    } else {
      return (
        <>
          <span>{t('maksamatta')}</span>
        </>
      )
    }
  }

  return (
    <Panel>
      <h2>{t('title')}</h2>
      {stateText()}
      <Maksu lasku={lasku} />
      <MaksaButton lasku={lasku}></MaksaButton>
    </Panel>)
}

export default KkHakemusmaksuPanel