import type { Metadata } from "next";
import { oppijaTheme } from "@opetushallitus/oph-design-system/next/theme"
import { CssBaseline, ThemeProvider } from '@mui/material'
import { AppRouterCacheProvider} from "@mui/material-nextjs/v13-appRouter"
import { CSSProperties, ReactNode } from "react"
import { NextIntlClientProvider } from "next-intl";
import { getMessages } from "next-intl/server";
import { TopBar } from "@/app/components/TopBar";
import { Locale } from "@/app/lib/types";

export const metadata: Metadata = {
  title: "Maksut",
  description: "Hakemusmaksujen käyttöliittymä"
};

export default async function LocaleLayout({
  children,
  params: {locale}
}: Readonly<{
  children: ReactNode;
  params: {locale: string};
}>) {
  const messages = await getMessages();

  const bodyStyle: CSSProperties = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    backgroundColor: '#F6F6F6',
  }

  return (
    <html lang={locale}>
      <body style={bodyStyle}>
        <AppRouterCacheProvider>
          <NextIntlClientProvider messages={messages}>
            <ThemeProvider theme={oppijaTheme}>
              <CssBaseline/>
              <TopBar lang={locale as Locale}></TopBar>
              {children}
            </ThemeProvider>
          </NextIntlClientProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  );
}
