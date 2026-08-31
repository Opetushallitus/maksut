import React, { ReactNode } from 'react';
import {
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  List,
  ListItem,
} from '@mui/material';
import {
  OphButton,
  ophColors,
  OphLink,
  OphTypography,
} from '@opetushallitus/oph-design-system';
import { useMessages, useTranslations } from 'use-intl';
import CloseIcon from '@mui/icons-material/Close';
import Grid from '@mui/material/Grid2';

const dialogStyle = {
  opacity: '1',
  padding: '24px',
  borderRadius: '4px',
  bordered: 'false',
  bgcolor: 'background.paper',
  boxShadow: '3px 3px 12px 0px #00000040',
  gap: '8px',
  p: 4,
};

export default function KkHakemusmaksuTerms({
  linkChildren,
  onAgreeTerms,
}: {
  linkChildren: ReactNode;
  onAgreeTerms: () => void;
}) {
  const [open, setOpen] = React.useState(false);

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const handleAgreeAndClose = () => {
    onAgreeTerms();
    setOpen(false);
  };

  const t = useTranslations('KkHakemusmaksuTerms');

  const messages = useMessages();
  const listItems = messages.KkHakemusmaksuTerms;
  if (typeof listItems === 'string') {
    return;
  }

  return (
    <>
      <OphLink
        component={'button'}
        underline="hover"
        onClick={handleOpen}
        sx={{ translate: '0 -1px' }}
      >
        {linkChildren}
      </OphLink>
      <Dialog
        open={open}
        title={t('title')}
        scroll={'paper'}
        onClose={handleClose}
        aria-labelledby="modal-modal-title"
        aria-describedby="modal-modal-description"
        maxWidth="md"
        style={dialogStyle}
      >
        <DialogTitle variant={'h1'} id="modal-modal-title">
          {t('title')}
        </DialogTitle>
        <IconButton
          aria-label="close"
          onClick={handleClose}
          sx={{
            position: 'absolute',
            right: 16,
            top: 16,
            color: ophColors.grey900,
          }}
        >
          <CloseIcon />
        </IconButton>
        <DialogContent id="modal-modal-description" sx={{ pt: 0, pr: 6 }}>
          {t.rich('body', {
            p: (chunks) => (
              <OphTypography variant={'body1'} sx={{ marginBottom: 2 }}>
                {chunks}
              </OphTypography>
            ),
            h2: (chunks) => (
              <OphTypography variant={'h2'}>{chunks}</OphTypography>
            ),
            ol: (chunks) => (
              <List component="ol" sx={{ listStyleType: 'decimal', pl: 5 }}>
                {chunks}
              </List>
            ),
            li: (chunks) => {
              const rivi = chunks?.toString();
              const alku = Number(rivi?.split('.')[0].trim());
              const loppu = rivi?.substring(rivi?.indexOf('.') + 1).trim();
              return (
                <ListItem value={alku} sx={{ display: 'list-item' }}>
                  <OphTypography variant={'body1'}>{loppu}</OphTypography>
                </ListItem>
              );
            },
          })}
          <Grid
            container
            spacing={2}
            justifyContent="flex-end"
            alignItems="flex-end"
          >
            <Grid size={'auto'}>
              <OphButton
                onClick={handleClose}
                variant={'outlined'}
                sx={{ py: '8px', px: '16px' }}
              >
                {t('sulje')}
              </OphButton>
            </Grid>
            <Grid size={'auto'}>
              <OphButton
                onClick={handleAgreeAndClose}
                variant={'contained'}
                sx={{ py: '8px', px: '16px' }}
              >
                {t('nappi')}
              </OphButton>
            </Grid>
          </Grid>
        </DialogContent>
      </Dialog>
    </>
  );
}
