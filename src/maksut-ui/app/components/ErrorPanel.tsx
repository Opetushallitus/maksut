'use client'

import Panel from "@/app/components/Panel";
import AlertIcon from "@/app/components/AlertIcon";

const ErrorPanel = ({children}: {children: React.ReactNode}) => {
  return (
    <div style={{marginTop: '72px'}}>
      <Panel>
        <AlertIcon></AlertIcon>
        {children}
      </Panel>
    </div>
  )
}

export default ErrorPanel