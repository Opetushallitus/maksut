'use client';

import useSWR from 'swr';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useLocale, useTranslations } from 'use-intl';
import {
  OphButton,
  OphLink,
  OphTypography,
} from '@opetushallitus/oph-design-system';
import { backendUrl } from '@/app/lib/configurations';
import ErrorPanel from '@/app/components/ErrorPanel';

const fetcher = (url: string) => fetch(url).then((r) => r.json());

export default function ErrorPage() {
  const [searchParams] = useSearchParams();
  const secret = searchParams.get('secret') ?? '';
  const navigate = useNavigate();
  const locale = useLocale();
  const t = useTranslations('PaymentError');

  const { data } = useSWR(
    secret ? `${backendUrl}/lasku-contact?secret=${secret}` : null,
    fetcher,
  );

  const contact = data?.contact ?? 'recognition@oph.fi';

  return (
    <ErrorPanel>
      <OphTypography variant={'h2'} component={'h1'}>
        {t('header')}
      </OphTypography>
      <OphTypography>{t('body1')}</OphTypography>
      <OphTypography>{t('body2')}</OphTypography>
      <OphTypography>
        {`${t('body3')} `}
        <OphLink href={`mailto:${contact}`}>{contact}</OphLink>.
      </OphTypography>
      <OphButton
        variant={'contained'}
        onClick={() => navigate(`/${locale}?secret=${secret}`)}
      >
        {t('returnButton')}
      </OphButton>
    </ErrorPanel>
  );
}
