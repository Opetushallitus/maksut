import { backendUrl } from '@/app/lib/configurations';
import { Lasku } from '@/app/lib/types';
import { notFound } from 'next/navigation';

export const fetchLaskutBySecret = async (
  secret: string | undefined,
): Promise<Lasku[]> => {
  if (!secret) {
    notFound();
  }
  const response = await fetch(
    `${backendUrl}/laskut-by-secret?secret=${secret}`,
    { cache: 'no-cache' },
  );
  if (response.ok) {
    return await response.json();
  } else if (response.status === 404) {
    notFound();
  }
  throw Error(response.statusText);
};

export const fetchLaskuContact = async (
  secret: string | undefined,
): Promise<{ contact: string }> => {
  if (!secret) {
    notFound();
  }
  const response = await fetch(`${backendUrl}/lasku-contact?secret=${secret}`, {
    cache: 'no-cache',
  });
  if (response.ok) {
    return await response.json();
  } else if (response.status === 404) {
    notFound();
  }
  throw Error(response.statusText);
};
