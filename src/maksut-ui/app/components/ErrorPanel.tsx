'use client'

import { useTranslations } from "use-intl";
import { Box } from "@mui/material";
import Image from "next/image";
import Panel from "@/app/components/Panel";
import PanelContent from "@/app/components/PanelContent";
import AlertIcon from "@/app/components/AlertIcon";

const ErrorPanel = ({title, content}: {title?: string, content?: string}) => {
  return (
    <Panel>
      <AlertIcon></AlertIcon>
      <h2>{title || ''}</h2>
      <span>{content || ''}</span>
    </Panel>
  )
}

export default ErrorPanel