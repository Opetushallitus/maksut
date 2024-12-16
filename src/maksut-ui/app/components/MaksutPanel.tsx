import { Lasku } from '@/app/lib/types';
import TutuPanel from '@/app/components/TutuPanel';
import AstuPanel from '@/app/components/AstuPanel';
import KkHakemusmaksuPanel from '@/app/components/KkHakemusmaksuPanel';
import { notFound } from 'next/navigation';

export default function MaksutPanel({
  laskut,
  secret,
}: {
  laskut: Lasku[];
  secret: string;
}) {
  const activeLasku = laskut.find((lasku) => lasku.secret === secret);

  switch (activeLasku?.origin) {
    case 'tutu':
      return <TutuPanel laskut={laskut} activeLasku={activeLasku} />;
    case 'astu':
      return <AstuPanel lasku={activeLasku} />;
    case 'kkhakemusmaksu':
      return <KkHakemusmaksuPanel lasku={activeLasku} />;
    default:
      notFound();
  }
}
