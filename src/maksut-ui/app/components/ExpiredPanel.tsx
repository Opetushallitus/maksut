'use client'

import ErrorPanel from "@/app/components/ErrorPanel";
import { useTranslations } from "use-intl";
import { OphLink } from "@opetushallitus/oph-design-system";

const ExpiredPanel = ({contact}: {contact: string | undefined}) => {
  const t = useTranslations('ExpiredPanel')

  return (
    <ErrorPanel>
      <h2>{t('header')}</h2>
      <span>{t('body1')}</span>
      <span>{t('body2')}<OphLink href={`mailto:${contact}`}>{contact}</OphLink>.</span>
    </ErrorPanel>
  )
}

export default ExpiredPanel;