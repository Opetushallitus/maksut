import React, { useState } from 'react';
import { Box, Checkbox, FormControlLabel } from '@mui/material';
import { useTranslations } from 'use-intl';
import { Lasku } from '@/app/lib/types';
import MaksaButton from '@/app/components/MaksaButton';
import MaksuTerms from '@/app/components/MaksuTerms';

export default function MaksuTermsAgreementForm({
  lasku,
}: {
  lasku: Lasku;
}): React.JSX.Element {
  const [agreed, setAgreed] = useState<boolean>(false);

  let t;
  switch (lasku.origin) {
    case 'kkhakemusmaksu':
      t = useTranslations('KkHakemusmaksuPanel');
      break;
    case 'astu':
      t = useTranslations('AstuPanel');
      break;
    case 'tutu':
      t = useTranslations('TutuPanel');
      break;
  }

  // Type the change event for the checkbox element
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>): void => {
    setAgreed(event.target.checked);
  };

  const onAgreeTerms = () => {
    setAgreed(true);
  };

  if (lasku.status === 'active') {
    return (
      <>
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            gap: 2,
            alignItems: 'flex-start',
          }}
        >
          <FormControlLabel
            control={
              <Checkbox
                checked={agreed}
                onChange={handleChange}
                color="primary"
              />
            }
            label={t.rich('hyvaksynta', {
              link: (chunks) => (
                <MaksuTerms
                  linkChildren={chunks}
                  onAgreeTerms={onAgreeTerms}
                  origin={lasku.origin}
                />
              ),
            })}
          />
        </Box>
        <MaksaButton lasku={lasku} termsAgreed={agreed}></MaksaButton>
      </>
    );
  }
  return <></>;
}
