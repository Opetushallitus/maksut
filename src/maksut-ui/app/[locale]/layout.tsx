import type { Metadata } from "next";
import '../globals.css'
import { oppijaTheme } from "@opetushallitus/oph-design-system/next/theme"
import { CssBaseline, ThemeProvider } from '@mui/material'
import { AppRouterCacheProvider} from "@mui/material-nextjs/v13-appRouter"
import { ReactNode } from "react"
import { NextIntlClientProvider } from "next-intl";
import { getMessages } from "next-intl/server";

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
  console.log(messages);

  return (
    <html lang={locale}>
      <body>
        <AppRouterCacheProvider>
          <NextIntlClientProvider messages={messages}>
            <ThemeProvider theme={oppijaTheme}>
              <CssBaseline/>
              {children}
            </ThemeProvider>
          </NextIntlClientProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  );
}
