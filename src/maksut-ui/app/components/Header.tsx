import { Box } from "@mui/material";
import { Lasku } from "@/app/lib/types";
import { getTranslations } from "next-intl/server";

const Header = async ({lasku}: {lasku: Lasku}) => {
  const t = await getTranslations('Header');

  return (
    <Box style={{textAlign: 'center'}}>
      <h3>{`${lasku.first_name} ${lasku.last_name}`}</h3>
      <h1>{t('title')}</h1>
    </Box>
  )
}

export default Header;