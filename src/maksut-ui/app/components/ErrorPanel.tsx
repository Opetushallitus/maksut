'use client';

import Panel from '@/app/components/Panel';
import AlertIcon from '@/app/components/AlertIcon';

const ErrorPanel = ({ children }: { children: React.ReactNode }) => {
  return (
    <Panel>
      <AlertIcon></AlertIcon>
      {children}
    </Panel>
  );
};

export default ErrorPanel;
