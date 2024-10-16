'use client'

import { Box, useTheme } from "@mui/material";
import { ReactNode } from "react";

const PanelContent = ({children}: {children: ReactNode}) => {
  const theme = useTheme()

  return (
    <Box style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      textAlign: 'center',
      gap: theme.spacing(2),
    }}>
      {children}
    </Box>
  )
}

export default PanelContent