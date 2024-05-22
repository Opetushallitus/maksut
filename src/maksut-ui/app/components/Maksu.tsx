import styles from "@/app/maksut/page.module.css";
import { Lasku } from "@/app/lib/types";

const Maksu = ({lasku}: {lasku: Lasku}) => {
  return <div className={styles.maksu}>
    Päätösmaksu<br/>
    Tila {lasku.status}<br/>
    Määrä {`${lasku.amount}€`}<br/>
    Eräpäivä {`${lasku.due_date}`}<br/>
  </div>
}

export default Maksu