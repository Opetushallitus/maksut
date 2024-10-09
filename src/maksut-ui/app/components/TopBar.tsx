'use client'

import { AppBar, Box, useTheme } from "@mui/material";
import Image from "next/image";
import { Locale } from "@/app/lib/types";

export const TopBar = ({lang}: {lang: Locale} ) => {
  const theme = useTheme()
  return (
    <AppBar position='static' style={{padding: theme.spacing(3, 0)}}>
      <Box style={{margin: 'auto', width: '1000px'}}>
        <Image src={`/maksut-ui/opintopolku_logo_header_${lang}.svg`} alt="Opintopolku" height={31} width={224}/>
      </Box>
    </AppBar>
  )
}