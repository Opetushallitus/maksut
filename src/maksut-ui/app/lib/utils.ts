import { Locale, LocalizedString } from "@/app/lib/types";

export function translateLocalizedString(
  translated: LocalizedString | undefined,
  userLanguage: Locale = 'fi',
): string {
  if (!translated) {
    return ''
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
  return translated.sv || '';
}