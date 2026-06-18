import { CSSProperties } from 'react';
import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
  useParams,
} from 'react-router-dom';
import { IntlProvider } from 'use-intl';
import { CssBaseline } from '@mui/material';
import { OphThemeProvider } from '@opetushallitus/oph-design-system/theme';
import useSWR from 'swr';
import { TopBar } from '@/app/components/TopBar';
import ErrorPage from '@/app/components/ErrorPage';
import MainPage from '@/app/components/MainPage';
import NotFoundPage from '@/app/components/NotFoundPage';
import { Locale } from '@/app/lib/types';

const locales: Locale[] = ['fi', 'sv', 'en'];
const defaultLocale: Locale = 'fi';

const fetcher = (url: string) => fetch(url).then((r) => r.json());

const pageStyle: CSSProperties = {
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  backgroundColor: '#F6F6F6',
  minWidth: '100%',
  minHeight: '100vh',
};

function LocaleLayout() {
  const { locale } = useParams<{ locale: string }>();
  const validLocale: Locale = locales.includes(locale as Locale)
    ? (locale as Locale)
    : defaultLocale;

  const { data: messages } = useSWR(
    `/maksut/api/localisation/${validLocale}`,
    fetcher,
  );

  return (
    <IntlProvider locale={validLocale} messages={messages ?? {}}>
      <OphThemeProvider lang={validLocale} variant="opintopolku">
        <CssBaseline />
        <div style={pageStyle}>
          <TopBar lang={validLocale} />
          <main style={pageStyle}>
            <Routes>
              <Route index element={<MainPage />} />
              <Route path="error" element={<ErrorPage />} />
              <Route path="*" element={<NotFoundPage />} />
            </Routes>
          </main>
        </div>
      </OphThemeProvider>
    </IntlProvider>
  );
}

export default function App() {
  return (
    <BrowserRouter basename="/maksut">
      <Routes>
        <Route path="/" element={<Navigate to="/fi" replace />} />
        <Route path=":locale/*" element={<LocaleLayout />} />
        <Route path="*" element={<Navigate to="/fi" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
