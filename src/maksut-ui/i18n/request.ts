import { getRequestConfig } from 'next-intl/server';
import { backendUrl } from '@/app/lib/configurations';
import { notFound } from 'next/navigation';
import { routing } from './routing';
import { Locale } from '@/app/lib/types';

const fetchLocalizations = async (locale = 'fi') => {
  const response = await fetch(`${backendUrl}/localisation/${locale}`, {
    cache: 'no-cache',
  });
  if (response.ok) {
    return await response.json();
  }
  notFound();
};

export default getRequestConfig(async ({ requestLocale }) => {
  let locale = await requestLocale;

  if (!locale || !routing.locales.includes(locale as Locale)) {
    locale = routing.defaultLocale;
  }

  return {
    locale,
    messages: await fetchLocalizations(locale),
  };
});
