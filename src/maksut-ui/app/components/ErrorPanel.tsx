'use client'

import Panel from "@/app/components/Panel";
import AlertIcon from "@/app/components/AlertIcon";

const ErrorPanel = ({title, content}: {title?: string, content?: string}) => {
  return (
    <Panel>
      <AlertIcon></AlertIcon>
      {title && <h2>{title}</h2>}
      {content && <span>{content}</span>}
    </Panel>
  )
}

export default ErrorPanel