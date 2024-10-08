'use client'

import { I18nextProvider } from "react-i18next";
import { createLocalization } from "@/app/i18n/localizations";

const localizations = createLocalization()

export default function LocalizationsProvider({ children} : { children: React.ReactNode }) {
  return (
    <I18nextProvider i18n={localizations}>{children}</I18nextProvider>
  )
}