'use client';

import { backendUrl } from '@/app/lib/configurations';
import { OphButton } from '@opetushallitus/oph-design-system';
import { Lasku } from '@/app/lib/types';
import { useLocale, useTranslations } from 'use-intl';

export default function MaksaButton({
  lasku,
  termsAgreed,
}: {
  lasku: Lasku;
  termsAgreed?: boolean;
}) {
  const locale = useLocale();
  const t = useTranslations('MaksutPanel');

  const termsAgreedParam =
    typeof termsAgreed === 'undefined'
      ? ''
      : `&terms-agreed=${termsAgreed}`;

  if (lasku.status === 'active') {
    return (
      <OphButton
        variant={'contained'}
        target={'_self'}
        disabled={termsAgreed === false}
        href={`${backendUrl}/lasku/${lasku.order_id}/maksa?secret=${lasku.secret}&locale=${locale}${termsAgreedParam}`}
      >
        {t('maksa')}
      </OphButton>
    );
  }
  return <></>;
}
