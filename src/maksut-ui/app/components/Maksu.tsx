'use client'

import styles from "@/app/page.module.css";
import { Lasku } from "@/app/lib/types";
import { useTranslations } from "@/app/i18n/useTranslations";

const Maksu = ({lasku, title}: {lasku: Lasku, title: string}) => {
  const {t} = useTranslations();

  const parseDate = (date: string) => {
    const [year, month, day] = date.split("-");
    return `${day}.${month}.${year}`;
  }

  return (
    <div className={styles.maksu}>
      <h4>{title}</h4>

      {t('maksu.tila')} {t(`maksu.tila.${lasku.status}`)}<br/>
      {t('maksu.summa')} {`${lasku.amount}€`}<br/>
      {lasku.status ?
        `${t('maksu.eräpäivä')} ${parseDate(lasku.due_date)}` :
        `${t('maksu.maksupäivä')} ${parseDate(lasku.paid_at)}`}<br/>
    </div>
  )
}

export default Maksu