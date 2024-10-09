'use client'

import styles from "@/app/[locale]/page.module.css";
import { Lasku } from "@/app/lib/types";
import { useTranslations } from "use-intl";

const Maksu = ({lasku}: {lasku: Lasku}) => {
  const t = useTranslations('Maksu');

  const parseDate = (date: string) => {
    const [year, month, day] = date.split("-");
    return `${day}.${month}.${year}`;
  }

  const title = () => {
    if (lasku.origin === 'astu') {
      return t('päätös')
    }
    if (lasku.origin === 'tutu') {
      if (lasku.order_id.endsWith('-1')) {
        return t('käsittely')
      }
      return t('päätös')
    }
    return t('käsittely')
  }

  return (
    <div className={styles.maksu}>
      <h4>{title()}</h4>

      {t('tila')} {t(`${lasku.status}`)}<br/>
      {t('summa')} {`${lasku.amount}€`}<br/>
      {lasku.status !== 'paid' ?
        `${t('eräpäivä')} ${parseDate(lasku.due_date)}` :
        `${t('maksupäivä')} ${parseDate(lasku.paid_at)}`}<br/>
    </div>
  )
}

export default Maksu