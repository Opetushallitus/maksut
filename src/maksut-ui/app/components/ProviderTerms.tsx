import { useTranslations } from 'use-intl';
import { Box } from '@mui/material';
import { OphLink, OphTypography } from '@opetushallitus/oph-design-system';
import { styled } from '@mui/system';
import { ophColors } from '@opetushallitus/oph-design-system';
import { Lasku } from '@/app/lib/types';
import { visuallyHidden } from '@mui/utils';

const TermsBox = styled(Box)(({ theme }) => ({
  marginTop: theme.spacing(2),
  display: 'flex',
  flexDirection: 'column',
  textAlign: 'left',
  gap: theme.spacing(2),
  [theme.breakpoints.down('lg')]: {
    margin: theme.spacing(2),
  },
  [theme.breakpoints.up('lg')]: {
    maxWidth: '1000px',
    width: '100%',
  },
  padding: theme.spacing(2, 4),
}));

export default function ProviderTerms({ lasku }: { lasku: Lasku }) {
  const t = useTranslations('ProviderTerms');

  if (lasku.status === 'active') {
    return (
      <TermsBox>
        <OphTypography
          variant={'body2'}
          style={{ fontWeight: 600, color: ophColors.grey600 }}
        >
          {t('title')}
        </OphTypography>
        <OphTypography
          variant={'body2'}
          style={{ fontWeight: 'regular', color: ophColors.grey600 }}
        >
          {t('provider')}
        </OphTypography>
        <OphTypography
          variant={'body2'}
          style={{ fontWeight: 'regular', color: ophColors.grey600 }}
        >
          {t.rich('address', {
            br: () => <br />,
            a: (chunks) => (
              <OphLink
                rel="noreferrer noopener"
                target="_blank"
                href={chunks as string}
                variant={'inherit'}
                sx={{ color: '#2f7302' }} // Oletusvärillä liian pieni kontrasti taustan kanssa
              >
                {chunks}
                <span style={visuallyHidden}>{t('uusiIkkuna')}</span>
              </OphLink>
            ),
          })}
        </OphTypography>
      </TermsBox>
    );
  }
  return <></>;
}
