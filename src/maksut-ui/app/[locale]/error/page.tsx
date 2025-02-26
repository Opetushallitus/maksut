import { fetchLaskuContact } from '@/app/lib/data';
import ErrorPageContent from '@/app/[locale]/error/content';

export default async function ErrorPage({
  searchParams,
}: {
  searchParams: Promise<{ secret?: string }>;
}) {
  const { secret } = await searchParams;

  const { contact } = secret
    ? await fetchLaskuContact(secret)
    : { contact: 'recognition@oph.fi' };

  return (
    <ErrorPageContent
      contact={contact}
      secret={secret || ''}
    ></ErrorPageContent>
  );
}
