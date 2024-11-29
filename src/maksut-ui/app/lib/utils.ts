import { Locale, LocalizedString } from '@/app/lib/types';

export function translateLocalizedString(
  translated: LocalizedString | undefined,
  userLanguage: Locale = 'fi',
  fallback = '',
): string {
  if (!translated) {
    return fallback;
  }
  const prop = userLanguage as keyof LocalizedString;
  const translation = translated[prop];
  if (translation && translation?.trim().length > 0) {
    return translated[prop] || '';
  } else if (translated.fi && translated.fi.trim().length > 0) {
    return translated.fi;
  } else if (translated.en && translated.en.trim().length > 0) {
    return translated.en;
  }
  return translated.sv || fallback;
}
