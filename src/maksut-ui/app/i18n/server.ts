import { createInstance } from 'i18next';
import { initReactI18next } from 'react-i18next/initReactI18next';
import HttpApi from 'i18next-http-backend';
import { getOptions } from './settings';
import { Locale } from "@/app/lib/types";

async function initI18next(lang: Locale) {
  const i18nInstance = createInstance();
  await i18nInstance
    .use(initReactI18next)
    .use(HttpApi)
    // Initialize i18next with the options we created earlier
    .init(getOptions(lang));

  return i18nInstance;
}

// This function will be used in our server components for the translation
export async function createTranslation(lang: Locale) {
  const i18nextInstance = await initI18next(lang);

  return {
    t: i18nextInstance.getFixedT(lang),
  };
}
