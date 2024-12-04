import Image from 'next/image';
import { Box } from '@mui/material';

const AlertIcon = () => {
  return (
    <Box
      style={{
        backgroundColor: '#cc2f1b',
        borderRadius: '50%',
        width: '60px',
        height: '60px',
        display: 'flex',
        alignItems: 'center',
      }}
    >
      <Image
        src={'/maksut-ui/alert.svg'}
        alt={'Alert icon'}
        width={40}
        height={40}
        style={{
          filter: 'invert(1)',
          margin: 'auto',
        }}
      ></Image>
    </Box>
  );
};

export default AlertIcon;
