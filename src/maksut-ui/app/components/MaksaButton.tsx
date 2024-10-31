'use client'

import { backendUrl } from "@/app/lib/configurations";
import { Button } from "@opetushallitus/oph-design-system";
import { Lasku } from "@/app/lib/types";
import { useLocale, useTranslations } from "use-intl";

const MaksaButton = ({lasku}: {lasku: Lasku}) => {
  const locale = useLocale()
  const t = useTranslations('MaksutPanel')

  if (lasku.status === 'active') {
    return (
      <Button
        variant={'contained'}
        href={`${backendUrl}/lasku/${lasku.order_id}/maksa?secret=${lasku.secret}&locale=${locale}`}
      >
        {t('maksa')}
      </Button>
    )
  }
  return <></>
}

export default MaksaButton