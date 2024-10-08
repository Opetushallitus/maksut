'use client';

import { useTranslation } from 'react-i18next';
import { useCallback } from 'react';
import { Locale, LocalizedString } from "@/app/lib/types";
import { translateLocalizedString } from "@/app/i18n/translationUtils";

export const useTranslations = () => {
  const { t, i18n } = useTranslation();
  const translateEntity = useCallback(
    (translateable?: LocalizedString) => {
      return translateable
        ? translateLocalizedString(translateable, i18n.language as Locale)
        : '';
    },
    [i18n],
  );

  return { t, translateEntity, language: i18n.language as Locale, i18n };
};
