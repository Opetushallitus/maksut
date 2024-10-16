import {getRequestConfig} from 'next-intl/server';
import { backendUrl } from "@/app/lib/configurations";
import {notFound} from 'next/navigation';
import {routing} from './routing';

const fetchLocalizations = async (locale: string) => {
  const response = await fetch(`${backendUrl}/localisation/${locale}`, {cache: "no-cache"})
  if (response.ok) {
    return await response.json()
  } else {
    throw Error(response.statusText)
  }
}

export default getRequestConfig(async ({locale}) => {
  if (!routing.locales.includes(locale as any)) notFound();

  return {
    messages: (await fetchLocalizations(locale))
  };
});