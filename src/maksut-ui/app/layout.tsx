import type { Metadata } from "next";
import './globals.css'
import { oppijaTheme } from "@opetushallitus/oph-design-system/next/theme"
import { CssBaseline, ThemeProvider } from '@mui/material'
import { AppRouterCacheProvider} from "@mui/material-nextjs/v13-appRouter"
import { ReactNode } from "react"

export const metadata: Metadata = {
  title: "Maksut",
  description: "Hakemusmaksujen käyttöliittymä"
};

export default function RootLayout({
  children,
}: Readonly<{
  children: ReactNode;
}>) {
  return (
    <html lang="fi">
      <body>
        <AppRouterCacheProvider>
          <ThemeProvider theme={oppijaTheme}>
            <CssBaseline/>
            {children}
          </ThemeProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  );
}
