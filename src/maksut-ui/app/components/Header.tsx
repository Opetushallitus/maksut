'use client'

import { Box } from "@mui/material";
import { Lasku } from "@/app/lib/types";
import { useTranslations } from "@/app/i18n/useTranslations";

const Header = ({lasku}: {lasku: Lasku}) => {
  const {t} = useTranslations();

  return (
    <Box style={{textAlign: 'center'}}>
      <h3>{`${lasku.last_name} ${lasku.first_name}`}</h3>
      <h1>{t('title')}</h1>
    </Box>
  )
}

export default Header;