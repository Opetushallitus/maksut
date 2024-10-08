import { Locale, LocalizedString } from "@/app/lib/types";

export const getLocalization = (localized: LocalizedString, locale: Locale) => {
  switch (locale) {
    case "fi": return localized.fi
    case "sv": return localized.sv
    case "en": return localized.en
  }
}

export const getFirstLocalization = (localized?: LocalizedString, locale?: Locale, fallback?: string) => {
  const priority: Array<Locale> = locale ? [locale, 'fi', 'en', 'sv'] : ['fi', 'en', 'sv']

  if (localized) {
    for (const p of priority) {
      const v = getLocalization(localized, p);

      if (v) {
        return v;
      }
    }
  }

  return fallback || null;
}