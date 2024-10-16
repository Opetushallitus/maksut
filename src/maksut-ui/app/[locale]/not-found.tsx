import { getTranslations } from "next-intl/server";
import { Box } from "@mui/material";
import Image from "next/image";

export default async function NotFound() {
  const t = await getTranslations('NotFound')

  return (
    <Box
      style={{
        margin: '64px',
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
            margin: 'auto',
            filter: 'invert(1)',
          }}>
        </Image>
      </Box>
      <h3>{t('title')}</h3>
      <span>{t('content')}</span>
    </Box>
  )
}