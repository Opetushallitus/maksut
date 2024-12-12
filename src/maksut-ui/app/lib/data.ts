import { backendUrl } from '@/app/lib/configurations';
import { Lasku } from '@/app/lib/types';
import { notFound } from 'next/navigation';

export const fetchLaskutBySecret = async (secret: string): Promise<Lasku[]> => {
  const response = await fetch(
    `${backendUrl}/laskut-by-secret?secret=${secret}`,
    { cache: 'no-cache' },
  );
  if (response.ok) {
    return await response.json();
  }
  notFound();
};

export const fetchLaskuContact = async (
  secret: string,
): Promise<{ contact: string }> => {
  const response = await fetch(`${backendUrl}/lasku-contact?secret=${secret}`, {
    cache: 'no-cache',
  });
  if (response.ok) {
    return await response.json();
  }
  notFound();
};
