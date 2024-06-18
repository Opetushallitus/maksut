import { Lasku } from "@/app/lib/types";
import styles from "@/app/page.module.css";
import Maksu from "@/app/components/Maksu";

const AstuPanel = ({lasku}: {lasku: Lasku}) => {
  return (
    <div className={styles.panel}>
      <h2>{lasku.reference}</h2>
      <span>Hakemuksesi käsitelty jne. loremipsum</span>
      <div className={styles.maksut}>
        <Maksu lasku={lasku}/>
      </div>
      {/*<Button>Siirry maksamaan</Button>*/}
    </div>)
}

export default AstuPanel