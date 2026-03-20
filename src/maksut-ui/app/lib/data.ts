import { backendUrl } from '@/app/lib/configurations';
import { Lasku } from '@/app/lib/types';
import { notFound } from 'next/navigation';

export const fetchLaskutBySecret = async (secret: string): Promise<Lasku[]> => {
  const url = `${backendUrl}/laskut-by-secret?secret=${secret}`;
  const response = await fetch(url, { cache: 'no-cache' });
  if (response.ok) {
    return await response.json();
  }
  console.error(
    `fetchLaskutBySecret: ${response.status} ${response.statusText} from ${url}`,
  );
  notFound();
};

export const fetchLaskuContact = async (
  secret: string,
): Promise<{ contact: string }> => {
  const url = `${backendUrl}/lasku-contact?secret=${secret}`;
  const response = await fetch(url, { cache: 'no-cache' });
  if (response.ok) {
    return await response.json();
  }
  console.error(
    `fetchLaskuContact: ${response.status} ${response.statusText} from ${url}`,
  );
  notFound();
};
