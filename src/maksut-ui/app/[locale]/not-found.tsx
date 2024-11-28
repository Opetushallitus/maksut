import { getTranslations } from "next-intl/server";
import { Box } from "@mui/material";
import Image from "next/image";
import Panel from "@/app/components/Panel";
import ErrorPanel from "@/app/components/ErrorPanel";

export default async function NotFound() {
  const t = await getTranslations('NotFound')

  return (
    <ErrorPanel>
      <h2>{t('header')}</h2>
      <span>{t('body1')}</span>
      <span>{t('body2')}</span>
    </ErrorPanel>
  )
}