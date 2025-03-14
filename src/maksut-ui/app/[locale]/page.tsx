import { fetchLaskuContact, fetchLaskutBySecret } from '@/app/lib/data';
import MaksutPanel from '@/app/components/MaksutPanel';
import { notFound, redirect } from 'next/navigation';
import Header from '@/app/components/Header';
import { routing } from '@/i18n/routing';
import { getLocale } from 'next-intl/server';
import ExpiredPanel from '@/app/components/ExpiredPanel';
import { Locale } from '@/app/lib/types';

export default async function Page({
  searchParams,
}: {
  searchParams: Promise<{ secret?: string }>;
}) {
  const { secret } = await searchParams;
  const locale = await getLocale();

  if (!routing.locales.includes(locale as Locale)) {
    redirect(`/fi?secret=${secret}`);
  }

  if (!secret) {
    notFound();
  }
  const laskut = await fetchLaskutBySecret(secret);
  const activeLasku = laskut.find((lasku) => lasku.secret === secret);

  if (!laskut.length || !activeLasku) {
    const { contact } = await fetchLaskuContact(secret);
    return <ExpiredPanel contact={contact} />;
  }

  return (
    <>
      <Header lasku={activeLasku}></Header>
      <MaksutPanel laskut={laskut} secret={secret} />
    </>
  );
}
