'use client';

import { TopBar } from "@/app/components/TopBar";
import ErrorPanel from "@/app/components/ErrorPanel";
import { OphNextJsThemeProvider } from "@opetushallitus/oph-design-system/next/theme";
import { CssBaseline } from "@mui/material";
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
        <OphNextJsThemeProvider lang={'en'} variant={'opintopolku'}>
          <CssBaseline/>
          <TopBar></TopBar>
          <ErrorPanel>
            <h2></h2>
          </ErrorPanel>
        </OphNextJsThemeProvider>
      </body>
    </html>
  );
}