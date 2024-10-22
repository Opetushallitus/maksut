import { fetchLaskutBySecret } from "@/app/lib/data";
import { Lasku } from "@/app/lib/types";
import MaksutPanel from "@/app/components/MaksutPanel";
import { notFound } from "next/navigation";
import Header from "@/app/components/Header";

export default async function Page({ searchParams }: {searchParams: {secret?: string}}) {
  const { secret } = searchParams

  if (!secret) {
    notFound()
  }

  const laskut: Array<Lasku> = await fetchLaskutBySecret(secret)
  const activeLasku = laskut.find((lasku) => lasku.secret === secret)

  if (!laskut.length || !activeLasku) {
    notFound()
  }


  return (
    <main>
      <Header lasku={activeLasku}></Header>
      <MaksutPanel laskut={laskut} secret={secret}/>
    </main>
  );
}
