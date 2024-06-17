import type {InitOptions} from 'i18next';
import type {HttpBackendOptions} from "i18next-http-backend";
import { Locale } from "@/app/lib/types";
import { backendUrl } from "@/app/lib/configurations";

export const FALLBACK_LOCALE = 'fi';
export const supportedLocales = ['en', 'fi', 'sv'] as const;

const backendOptions = (lang: Locale): HttpBackendOptions => {
  return { loadPath: (lang) => `${backendUrl}/localisation/${lang}`}
}
export function getOptions(lang: Locale = FALLBACK_LOCALE): InitOptions {
  return {
    debug: true, // Set to true to see console logs
    backend: backendOptions(lang),
    supportedLngs: supportedLocales,
    fallbackLng: FALLBACK_LOCALE,
    lng: lang,
  };
}