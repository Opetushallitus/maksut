'use client';

import { TopBar } from "@/app/components/TopBar";
import ErrorPanel from "@/app/components/ErrorPanel";
import { oppijaTheme } from "@opetushallitus/oph-design-system/next/theme";
import { CssBaseline, ThemeProvider } from "@mui/material";
import { CSSProperties } from "react";

export default function NotFound() {
  const bodyStyle: CSSProperties = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    backgroundColor: '#F6F6F6',
  }
  return (
    <html lang="en">
      <body style={bodyStyle}>
        <ThemeProvider theme={oppijaTheme}>
          <CssBaseline/>
          <TopBar></TopBar>
          <ErrorPanel title={'Locale not supported'}></ErrorPanel>
        </ThemeProvider>
      </body>
    </html>
  );
}