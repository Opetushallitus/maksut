// (defn circle-icon [index selected done]
// (let [bg-color (cond
// done colors/process-circle-bg-selected
// selected colors/process-circle-bg-selected
// :else colors/process-circle-bg)
// fg-color (cond
// (or selected done) colors/process-circle-fg-selected
// :else colors/process-circle-fg)
// border-color (cond
// selected colors/process-circle-border-selected
// :else colors/process-circle-border)
// dot-style       {:background-color bg-color
// :border (str "2px solid " border-color)
// :border-radius "22px" ;( border*2 + height + padding ) / 2
// :font-weight "600"
// :font-size "15px"
// :text-align "center"
// :font "bold 15px/13px Helvetica, Verdana, Tahoma"
// :height "25px"
// :padding "11px 3px 0 3px"
// :min-width "30px"
// :width "min-content"
// :margin "auto"
// :color fg-color}]
// [:div (use-style dot-style)
// (if done
//   [:span (use-style {:vertical-align "middle"}) [icon/done-bold]]
// (str index))]
// ))


// (defn process-map [state kasittely-status paatos-status]
// (let [header-active {:color colors/process-circle-text-selected
// :width "min-content"
// :white-space "nowrap"
// :margin "auto"}
// header-passive {:color colors/process-circle-text
// :width "min-content"
// :white-space "nowrap"
// :margin "auto"}
// ]
// [:div (use-style {:display "grid"
// :width "400px"
// :grid-template-columns "200px 200px"
//     ::stylefy/media {media-small {
//   :width "300px"
//   :grid-template-columns "150px 150px"}}
// :grid-row "auto auto"
// :grid-row-gap "10px"
// })
// [circle-icon 1 true (= kasittely-status :paid)]
// [circle-icon 2 (or (= state :kasittely-maksettu) (= state :paatos-maksettu)) (= paatos-status :paid)]
//
// (case state
//   :loading [:<>]
//   :invalid-secret [:<>]
//     :kasittely-maksamatta [:<>
//       [:div (use-style header-active) @(subscribe [:translation :tutu-panel/tila-käsittelymaksu])]
//       [:div (use-style header-passive) @(subscribe [:translation :tutu-panel/tila-käsittely])]]
//       :kasittely-maksettu [:<>
//       [:div (use-style header-passive) @(subscribe [:translation :tutu-panel/tila-käsittelymaksu])]
//       [:div (use-style header-active) @(subscribe [:translation :tutu-panel/tila-käsittely])]]
//       :paatos-maksamatta [:<>
//       [:div (use-style header-active) @(subscribe [:translation :tutu-panel/tila-käsittely])]
//       [:div (use-style header-passive) @(subscribe [:translation :tutu-panel/tila-päätösmaksu])]]
//       :paatos-maksettu [:<>
//       [:div (use-style header-passive) @(subscribe [:translation :tutu-panel/tila-käsittely])]
//       [:div (use-style header-active) @(subscribe [:translation :tutu-panel/tila-päätösmaksu])]])
//
//       ]))

import { Box, useTheme } from "@mui/material";
import DoneIcon from '@mui/icons-material/Done'
import { useTranslations } from "use-intl";
import { ReactNode } from "react";

type PaymentState = 'kasittelymaksamatta' | 'kasittelymaksettu' | 'paatosmaksamatta' | 'paatosmaksettu'

const CircleIcon = ({index, active, done}: {index: number, active: boolean, done: boolean}) => {
  const theme = useTheme()

  return (
    <Box style={{
      backgroundColor : active ? '#3A7A10' : '#ffffff',
      border: `2px solid ${active ? '#3A7A10' : '#aeaeae'}`,
      borderRadius: "50%",
      fontWeight: "600",
      height: theme.spacing(5),
      width: theme.spacing(5),
      color: active ? '#ffffff' : '#101010',
    }}>
      <Box style={{marginTop: theme.spacing(0.75)}}>
        {done ?
          <DoneIcon></DoneIcon> :
          <span>{index}</span>
        }
      </Box>
    </Box>
  )
}

const State = ({children}: {children: ReactNode}) => {
  return (
    <Box style={{
      display: 'flex',
      alignItems: 'center',
      flexDirection: 'column',
      width: '200px',
      gap: '10px',
    }}>
      {children}
    </Box>
  )
}

const activeHeaderStyle = {
  width: 'min-content',
  whiteSpace: 'nowrap',
  margin: 'auto',
  fontWeight: 400,
  color: '#3A7A10',
}

const passiveHeaderStyle = {
  ...activeHeaderStyle,
  color: '#353535'
}

const KasittelyState = ({state}: {state: PaymentState}) => {
  const t = useTranslations('TutuStateTracker')

  const header = () => {
    switch (state) {
      case 'kasittelymaksamatta':
        return <h4 style={activeHeaderStyle}>{t('käsittelymaksu')}</h4>
      case 'kasittelymaksettu':
        return <h4 style={passiveHeaderStyle}>{t('käsittelymaksu')}</h4>
      case 'paatosmaksamatta':
        return <h4 style={activeHeaderStyle}>{t('käsittely')}</h4>
      case 'paatosmaksettu':
        return <h4 style={passiveHeaderStyle}>{t('käsittely')}</h4>
    }
  }

  return (
    <State>
      <CircleIcon index={1} active={true} done={state !== 'kasittelymaksamatta'}></CircleIcon>
      {header()}
    </State>
  )
}

const PaatosState = ({state}: {state: PaymentState}) => {
  const t = useTranslations('TutuStateTracker')

  const header = () => {
    switch (state) {
      case 'kasittelymaksamatta':
        return <h4 style={passiveHeaderStyle}>{t('käsittely')}</h4>
      case 'kasittelymaksettu':
        return <h4 style={activeHeaderStyle}>{t('käsittely')}</h4>
      case 'paatosmaksamatta':
        return <h4 style={passiveHeaderStyle}>{t('päätösmaksu')}</h4>
      case 'paatosmaksettu':
        return <h4 style={activeHeaderStyle}>{t('päätösmaksu')}</h4>
    }
  }

  const active = state === 'kasittelymaksettu' || state === 'paatosmaksettu'
  const done = state === 'paatosmaksettu'

  return (
    <State>
      <CircleIcon index={2} active={active} done={done}></CircleIcon>
      {header()}
    </State>
  )
}

const TutuStateTracker = ({state}: {state: PaymentState}) => {
  const theme = useTheme()

  return (
    <Box style={{
      display: 'flex',
      flexDirection: 'row',
      marginBottom: theme.spacing(2),
    }}>
      <KasittelyState state={state}></KasittelyState>
      <PaatosState state={state}></PaatosState>
    </Box>
  )
}

export default TutuStateTracker