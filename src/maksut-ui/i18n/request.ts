import { getRequestConfig } from 'next-intl/server';
import { backendUrl } from '@/app/lib/configurations';

const fetchLocalizations = async (locale = 'fi') => {
  const response = await fetch(`${backendUrl}/localisation/${locale}`, {
    cache: 'no-cache',
  });
  if (response.ok) {
    return await response.json();
  } else {
    throw Error(response.statusText);
  }
};

export default getRequestConfig(async ({ requestLocale }) => {
  return {
    messages: await fetchLocalizations(await requestLocale),
  };
});
