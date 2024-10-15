'use client'

import { useTranslations } from "use-intl";
import { Box } from "@mui/material";
import Image from "next/image";

const ErrorPanel = () => {
  const t = useTranslations('NotFound')

  return (
    <Box
      style={{
        margin: '32px',
        backgroundColor: 'white',
        alignItems: 'center',
        display: 'flex',
        flexDirection: 'column',
        maxWidth: '1200px',
        filter: 'drop-shadow(0 1px 3px rgba(0,0,0,0.30))',
        minWidth: '800px',
        padding: '24px',
      }}>
      <Box style={{
        backgroundColor: '#cc2f1b',
        borderRadius: '50%',
        width: '60px',
        height: '60px',
        display: 'flex',
        alignItems: 'center',
      }}>
        <Image
          src={'/maksut-ui/alert.svg'}
          alt={t('imageAlt')}
          width={40}
          height={40}
          style={{
            filter: 'invert(1)',
            margin: 'auto',
          }}>
        </Image>
      </Box>
      <h2>{t('title')}</h2>
      <span>{t('content')}</span>
    </Box>
  )
}

export default ErrorPanel