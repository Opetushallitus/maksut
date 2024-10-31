import { getTranslations } from "next-intl/server";
import { Box } from "@mui/material";
import Image from "next/image";
import Panel from "@/app/components/Panel";
import ErrorPanel from "@/app/components/ErrorPanel";

export default async function NotFound() {
  const t = await getTranslations('NotFound')

  return (
    <ErrorPanel title={t('title')} content={t('content')}></ErrorPanel>
  )
}