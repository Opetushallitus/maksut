import type { Metadata } from 'next';
import { OphNextJsThemeProvider } from '@opetushallitus/oph-design-system/next/theme';
import { CssBaseline } from '@mui/material';
import { AppRouterCacheProvider } from '@mui/material-nextjs/v13-appRouter';
import { CSSProperties, ReactNode } from 'react';
import { NextIntlClientProvider } from 'next-intl';
import { getMessages } from 'next-intl/server';
import { TopBar } from '@/app/components/TopBar';
import { Locale } from '@/app/lib/types';
import { OphLanguage, ophColors } from '@opetushallitus/oph-design-system';

export const metadata: Metadata = {
  title: 'Maksut',
  description: 'Maksujen käyttöliittymä',
};

export default async function LocaleLayout({
  children,
  params,
}: Readonly<{
  children: ReactNode;
  params: Promise<{ locale: string }>;
}>) {
  const messages = await getMessages();
  const { locale } = await params;

  const bodyStyle: CSSProperties = {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    backgroundColor: ophColors.grey50,
    minWidth: '100%',
  };

  return (
    <html lang={locale} style={{ margin: 0, padding: 0, width: '100%' }}>
      <body style={bodyStyle}>
        <AppRouterCacheProvider>
          <NextIntlClientProvider messages={messages}>
            <OphNextJsThemeProvider
              lang={locale as OphLanguage}
              variant={'opintopolku'}
            >
              <CssBaseline />
              <TopBar lang={locale as Locale}></TopBar>
              <main>{children}</main>
            </OphNextJsThemeProvider>
          </NextIntlClientProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  );
}
