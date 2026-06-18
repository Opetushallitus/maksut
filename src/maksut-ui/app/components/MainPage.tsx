'use client';

import useSWR from 'swr';
import { useSearchParams } from 'react-router-dom';
import { Lasku } from '@/app/lib/types';
import { backendUrl } from '@/app/lib/configurations';
import Header from '@/app/components/Header';
import MaksutPanel from '@/app/components/MaksutPanel';
import ExpiredPanel from '@/app/components/ExpiredPanel';
import NotFoundPage from '@/app/components/NotFoundPage';

const fetcher = async (url: string) => {
  const res = await fetch(url);
  if (!res.ok) throw new Error(res.statusText);
  return res.json();
};

export default function MainPage() {
  const [searchParams] = useSearchParams();
  const secret = searchParams.get('secret');

  const {
    data: laskut,
    isLoading,
    error,
  } = useSWR<Lasku[]>(
    secret ? `${backendUrl}/laskut-by-secret?secret=${secret}` : null,
    fetcher,
  );

  const activeLasku = laskut?.find((l) => l.secret === secret);
  const isExpired = laskut !== undefined && (!laskut.length || !activeLasku);

  const { data: contactData } = useSWR(
    isExpired && secret ? `${backendUrl}/lasku-contact?secret=${secret}` : null,
    fetcher,
  );

  if (!secret || error) return <NotFoundPage />;
  if (isLoading || !laskut) return null;
  if (isExpired) return <ExpiredPanel contact={contactData?.contact} />;
  if (!activeLasku) return null;

  return (
    <>
      <Header lasku={activeLasku} />
      <MaksutPanel laskut={laskut} secret={secret} />
    </>
  );
}
