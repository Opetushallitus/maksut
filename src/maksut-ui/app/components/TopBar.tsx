'use client';

import { AppBar, Box, useTheme } from '@mui/material';
import Image from 'next/image';
import { Locale } from '@/app/lib/types';
import { styled } from '@mui/system';

const StyledBox = styled(Box)(({ theme }) => ({
  margin: 'auto',
  [theme.breakpoints.down('lg')]: {
    margin: theme.spacing(2),
  },
  [theme.breakpoints.up('lg')]: {
    maxWidth: '1000px',
    width: '100%',
  },
  display: 'flex',
  flexDirection: 'column',
  justifyItems: 'center',
}));

export const TopBar = ({ lang }: { lang?: Locale }) => {
  const theme = useTheme();
  return (
    <AppBar position="static" style={{ padding: theme.spacing(3, 0) }}>
      <StyledBox>
        <Image
          src={`/maksut-ui/opintopolku_logo_header_${lang || 'fi'}.svg`}
          alt="Opintopolku"
          height={26}
          width={155}
          priority
        />
      </StyledBox>
    </AppBar>
  );
};
