'use client'

import { Lasku, Locale } from "@/app/lib/types";
import TutuPanel from "@/app/components/TutuPanel";
import AstuPanel from "@/app/components/AstuPanel";
import { Box, useTheme } from "@mui/material";
import { Button, colors } from "@opetushallitus/oph-design-system"
import { backendUrl } from "@/app/lib/configurations";
import { notFound } from "next/navigation";
import { useTranslations } from "use-intl";
import { getLocale } from "next-intl/server";

export const dynamic = 'force-dynamic';

export default function MaksutPanel({ laskut, secret, locale }: {laskut: Array<Lasku>, secret: string, locale: Locale }) {
  const theme = useTheme()
  const activeLasku = laskut.find((lasku) => lasku.secret === secret)
  const t = useTranslations('MaksutPanel')

  if (activeLasku === undefined) {
    notFound()
  }

  const panel = () => {
    switch (activeLasku.origin) {
      case 'tutu':
        return <TutuPanel laskut={laskut}/>
      case 'astu':
        return <AstuPanel lasku={activeLasku}/>
    }
  }

  return (
    <Box
      style={{
        margin: 'auto',
        backgroundColor: colors.white,
        alignItems: 'center',
        display: 'flex',
        flexDirection: 'column',
        padding: theme.spacing(1, 0),
      }}>
      {panel()}
      <Button
        variant={'contained'}
        href={`${backendUrl}/lasku/${activeLasku.order_id}/maksa?secret=${secret}&locale=${locale}`}
        disabled={activeLasku.status !== 'active'}
      >
        {t('maksa')}
      </Button>
    </Box>
  );
}
