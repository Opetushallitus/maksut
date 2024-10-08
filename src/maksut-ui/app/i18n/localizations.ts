'use client'

import FetchBackend from 'i18next-fetch-backend';
import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import { backendUrl, isDev } from "@/app/lib/configurations";

export const FALLBACK_LOCALE = 'fi';
export const SUPPORTED_LOCALES = ['en', 'fi', 'sv'] as const;

export const createLocalization = () => {
  // eslint-disable-next-line @typescript-eslint/no-floating-promises
  i18n
    .use(FetchBackend)
    .use(initReactI18next)
    .init({
      debug: isDev, // Set to true to see console logs
      backend: {
        loadPath: `${backendUrl}/localisation/{{lng}}`,
        requestOptions: {
          mode: 'no-cors',
        }
      },
      preload: SUPPORTED_LOCALES,
      supportedLngs: SUPPORTED_LOCALES,
      fallbackLng: FALLBACK_LOCALE,
      lng: FALLBACK_LOCALE,
    })
  return i18n;
};