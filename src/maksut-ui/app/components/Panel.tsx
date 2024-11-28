'use client'

import { Box, useTheme } from "@mui/material";
import { ophColors } from "@opetushallitus/oph-design-system";
import { ReactNode } from "react";
import PanelContent from "@/app/components/PanelContent";

const Panel = ({children}: {children: ReactNode}) => {
  const theme = useTheme()

  return (
    <Box
      style={{
        margin: `${theme.spacing(2)}`,
        backgroundColor: ophColors.white,
        alignItems: 'center',
        display: 'flex',
        flexDirection: 'column',
        maxWidth: '1200px',
        minWidth: '800px',
        filter: 'drop-shadow(0 1px 3px rgba(0,0,0,0.30))',
        padding: theme.spacing(2, 4),
      }}>
      <PanelContent>
        {children}
      </PanelContent>
    </Box>
  );
}

export default Panel