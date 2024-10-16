'use client'

import { useTranslations } from "use-intl";
import { Box } from "@mui/material";
import Image from "next/image";
import Panel from "@/app/components/Panel";
import PanelContent from "@/app/components/PanelContent";

const ErrorPanel = () => {
  const t = useTranslations('NotFound')

  return (
    <Panel>
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
      <PanelContent>
        <h2>{t('title')}</h2>
        <span>{t('content')}</span>
      </PanelContent>
    </Panel>
  )
}

export default ErrorPanel