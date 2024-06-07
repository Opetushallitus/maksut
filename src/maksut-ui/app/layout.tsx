import type { Metadata } from "next";
import './globals.css'
import { oppijaTheme } from "@opetushallitus/oph-design-system/next/theme"
import { Roboto} from "next/font/google";
import { ThemeProvider } from '@mui/material'
import { TopBar } from "@/app/components/TopBar";


const roboto = Roboto({
  weight: ['400', '500'],
  display: 'swap',
  subsets: ['latin'],
})
export const metadata: Metadata = {
  title: "Maksut",
  description: "Hakemusmaksujen käyttöliittymä"
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="fi" className={roboto.className}>
      <body>
        <ThemeProvider theme={oppijaTheme}>
          <header>
            <TopBar></TopBar>
          </header>
          {children}
        </ThemeProvider>
      </body>
    </html>
  );
}
