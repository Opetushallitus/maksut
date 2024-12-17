'use client';

import { Box } from '@mui/material';
import { ophColors } from '@opetushallitus/oph-design-system';
import { ReactNode } from 'react';
import PanelContent from '@/app/components/PanelContent';
import { styled } from '@mui/system';

const StyledBox = styled(Box)(({ theme }) => ({
  marginTop: theme.spacing(4),
  backgroundColor: ophColors.white,
  alignItems: 'center',
  display: 'flex',
  flexDirection: 'column',
  [theme.breakpoints.down('lg')]: {
    margin: theme.spacing(2),
  },
  [theme.breakpoints.up('lg')]: {
    maxWidth: '1000px',
    width: '100%',
  },
  filter: 'drop-shadow(0 1px 3px rgba(0,0,0,0.30))',
  padding: theme.spacing(2, 4),
}));

const Panel = ({ children }: { children: ReactNode }) => {
  return (
    <StyledBox>
      <PanelContent>{children}</PanelContent>
    </StyledBox>
  );
};

export default Panel;
