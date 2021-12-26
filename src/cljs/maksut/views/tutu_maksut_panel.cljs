(ns maksut.views.tutu-maksut-panel
  (:require [maksut.components.common.button :as button]
            [maksut.components.common.label :as label]
            [maksut.components.common.panel :as p]
            [maksut.components.common.headings :as h]
            [maksut.components.common.material-icons :as icon]
            [maksut.components.common.svg :as svg]
            [maksut.styles.styles-colors :as colors]
            [maksut.events.maksut-events :as maksut-events]
            [maksut.subs.maksut-subs :as maksut-subs]
            [maksut.dates.date-parser :refer [format-date]]
            [schema.core :as s]
            [re-frame.core :refer [dispatch subscribe]]
            [reagent.dom :refer [dom-node]]
            [reagent.core :as reagent]
            [stylefy.core :as stylefy :refer [use-style]]))

;Note. Tämä näkymä on Tutu käyttökohteen mukainen, jos/kun Maksuja käytetään muuhunkin tarkoitukseen,
;tee yleiskäyttöisempi versio tästä. Suurin ero tässä on että Tutu-maksuprosessi koostuu kahdesta erillisestä
;maksusta jotka kummatkin esitellään tässä näkymässä kun taas normaalimmat käyttäkohteet vaativat vain yhden maksun.

(def ^:private grid-styles
  {:display  "grid"
   :justify-items "center"
   :grid-gap "15px"})

;(defn on-save-button-click [input-value saved-operation-type] ;TODO saved-operation-type - add schema for possible vals, [:create :rename]
;  (case saved-operation-type
;        :create (dispatch [hakukohderyhma-events/hakukohderyhma-persisted input-value])
;        :rename (dispatch [hakukohderyhma-events/hakukohderyhma-renamed input-value])))

;(defn on-delete-button-click [deleted-hakukohderyhma]
;  (dispatch [hakukohderyhma-events/hakukohderyhma-deleted deleted-hakukohderyhma]))

;Stylejä mitä vielä tarttee
;(def trash-can-icon (svg/img-icon "trash-can" {:height "20px"
;                                               :width  "16px"
;                                               :margin "6px 5px 0px 5px"}))

;(defn- hakukohderyhma-link [{:keys [cypressid
;                                    style-prefix
;                                    label
;                                    on-click
;                                    disabled?]}]
;  [:span
;   [button/text-button {:cypressid    cypressid
;                   :disabled?    disabled?
;                   :style-prefix style-prefix
;                   :label        label
;                   :on-click     on-click}]])

(def ^:private lasku-style
  {:border-radius "3px"
   :padding "20px"
   :min-width "320px"
   :background-color colors/invoice-bg})

(def ^:private dot-style
  {:height "10px"
   :width "10px"
   :border-radius "50%"
   :display "inline-block"})


(defn on-maksa-click [x]
  (dispatch [maksut-events/get-payment-form x]))


(defn invoice-status-indicator [status]
  (let [c (get-in colors/invoice-status [status])
        dot-style (merge dot-style {:background-color (:dot c)
                                    :margin-right "5px"})
        style {:padding "6px 15px 6px 15px"
                    :border-radius "2px"
                    :font-weight "700"
                    :font-size   "14px"
                    :white-space "nowrap"
                    :width "min-content"
                    :margin-left "auto"
                    :background-color (:background-color c)
                    :color (:text c)}
        dot (:dot c)
        status-text (case status
                          :active "Avoinna"
                          :paid "Maksettu"
                          :overdue "Erääntynyt"
                          (str status))]
                  [:div (use-style style)
                    [:div (use-style dot-style)]
                    status-text]
    ))

(defn circle-icon [index selected done]
  (let [bg-color (cond
                  done colors/process-circle-bg-selected
                  selected colors/process-circle-bg-selected
                  :else colors/process-circle-bg)
        fg-color (cond
                  (or selected done) colors/process-circle-fg-selected
                  :else colors/process-circle-fg)
        border-color (cond
                       selected colors/process-circle-border-selected
                       :else colors/process-circle-border)
        dot-style       {:background-color bg-color
                         :border (str "2px solid " border-color)
                         :border-radius "22px" ;( border*2 + height + padding ) / 2
                         :font-weight "600"
                         :font-size "15px"
                         :text-align "center"
                         :font "bold 15px/13px Helvetica, Verdana, Tahoma"
                         :height "25px"
                         :padding "11px 3px 0 3px"
                         :min-width "30px"
                         :width "min-content"
                         :margin "auto"
                         :color fg-color}
        style {:padding "6px 15px 6px 15px"
               :border-radius "2px"
               :white-space "nowrap"
               }]
     [:div (use-style dot-style)
      (if done
        [:span (use-style {:vertical-align "middle"}) [icon/done-bold]]
        (str index))]
    ))

(s/defschema TutuMaksuState
  (s/enum
    :loading
    :invalid-secret
    :kasittely-maksamatta
    :kasittely-maksettu
    :paatos-maksamatta
    :paatos-maksettu))

(defn process-map [state kasittely-status paatos-status]
  (let [header-active {:color colors/process-circle-text-selected
                       :width "min-content"
                       :white-space "nowrap"
                       :margin "auto"}
        header-passive {:color colors/process-circle-text
                       :width "min-content"
                       :white-space "nowrap"
                       :margin "auto"}
        ]
  [:div (use-style {:display "grid"
                    :width "400px"
                    :grid-template-columns "200px 200px"
                    :grid-row "auto auto"
                    :grid-row-gap "10px"
                    })
     [circle-icon 1 true (= kasittely-status :paid)]
     ;TODO consider fixing this in the end if there is time
     ;[:div (use-style {:justify-content "center"
     ;                  :text-align "center"
     ;                  :height "max-content"
     ;                  :vertical-align "middle"})
     ; [:div (use-style {
     ;                    :text-align "center"
     ;                    :border-top (str "2px dashed " colors/process-circle-border)
     ;                    :height "2px"
     ;                    :width "150px"})
     ;  ]
      ;]
     [circle-icon 2 (or (= state :kasittely-maksettu) (= state :paatos-maksettu)) (= paatos-status :paid)]
     ;]

   (case state
     :loading [:<>]
     :invalid-secret [:<>]
     :kasittely-maksamatta [:<>
       [:div (use-style header-active) "Käsittelymaksu"]
       [:div (use-style header-passive) "Hakemuksen käsittely"]]
     :kasittely-maksettu [:<>
       [:div (use-style header-passive) "Käsittelymaksu"]
       [:div (use-style header-active) "Hakemuksen käsittely"]]
     :paatos-maksamatta [:<>
       [:div (use-style header-active) "Hakemuksen käsittely"]
       [:div (use-style header-passive) "Päätösmaksu"]]
     :paatos-maksettu [:<>
       [:div (use-style header-passive) "Hakemuksen käsittely"]
       [:div (use-style header-active) "Päätösmaksu"]])

   ]))

(defn invoice-item [header invoice]
  (let [editing (reagent/atom false)
        value-style {:text-align "right"
                     :margin-bottom "10px"}
        separator-style {
                :grid-column "span 2"
                :border-bottom "1px solid #cecfd0"
                :margin-bottom "10px"
                :padding-bottom "-20px"
                }]
    (fn [header {:keys [order_id first_name last_name amount due_date status paid_at]} invoice]
      ^{:key (:order_id order_id)}
      [:div (use-style lasku-style)
       [:div (use-style {:margin-bottom "20px"
                         :text-align "center"})
        [:span (use-style (merge h/h3-styles value-style)) header]
       ]
       [:div (use-style { :display "grid"
                          :justify-content "space-between"
                          :grid-template-columns "1fr 1fr"})

        [:span "Tila"]
        [:div (use-style value-style) [invoice-status-indicator status]]

        [:span (use-style separator-style)]

        [:span "Määrä"]
        [:span (use-style (merge h/h3-styles value-style {:padding-top "0px"})) (str amount "€")]

        [:span (use-style separator-style)]

        (case status
              :active
              [:<>
                [:span "Eräpäivä"]
                [:span (use-style value-style) (format-date due_date)]]
              :paid
              [:<>
                [:span "Maksupäivä"]
                [:span (use-style value-style) (format-date paid_at)]]
              :overdue
              [:<>
                [:span "Eräpäivä"]
                [:span (use-style value-style) (format-date due_date)]]
              )
        ]
       ])))

(defn paytrail-payment-form [data]
  (reagent/create-class
   {:display-name "paytrail-payment-form"
    :component-did-mount
    (fn [comp]
      (let [form (dom-node comp)]
        (.requestSubmit form)))
    :reagent-render
    (fn []
      (let [{:keys [uri params]} data]
        [:form {:action uri}
         (for [[k v] params] ^{:key k}[:input {:type "hidden" :name k :value v}])]))}))

(defn laskut-container [laskut]
  (let [is-loading (subscribe [maksut-subs/maksut-is-loading])
        oid-suffix-matcher #(first (filter (fn [x] (clojure.string/ends-with? (:order_id x) %)) laskut))
        kasittely (oid-suffix-matcher "-1")
        paatos (oid-suffix-matcher "-2")
        kasittely-status (:status kasittely)
        paatos-status (:status paatos)
        kasittely-paid (= kasittely-status :paid)
        paatos-paid (= paatos-status :paid)
        has-paatos (some? paatos-status)
        state (cond
               @is-loading :loading
               (nil? laskut) :invalid-secret
               (and (not has-paatos) (not kasittely-paid)) :kasittely-maksamatta
               (and (not has-paatos) kasittely-paid) :kasittely-maksettu
               (and has-paatos (not paatos-paid)) :paatos-maksamatta
               (and has-paatos paatos-paid) :paatos-maksettu)

        pay-id (cond
                  (and (= state :kasittely-maksamatta) (= kasittely-status :active)) (:order_id kasittely)
                  (and (= state :paatos-maksamatta) (= paatos-status :active)) (:order_id paatos))
        show-process (case state
                           :invalid-secret false
                           :loading false
                           true)
        payment-form (subscribe [maksut-subs/maksut-payment-form])
        lasku-container-style {:display "flex"
                               :grid-gap "10px"
                               :flex-wrap "wrap"
                               :justify-content "center" }
        button-style {:margin-top "20px"
                      :margin-bottom "30px"
                      :background-color "#007373"
                      ::stylefy/mode {:hover {:background-color "#254905"}} ; Green-900
                      :padding "10px 25px 10px 25px"
                      :cursor "pointer"
                      :color "#ffffff"}]
    [:<>
     (when show-process [process-map state kasittely-status paatos-status])
      [h/heading {:cypressid (str "laskut-state-header")
                  :level     :h4}
        (case state
          :loading ""
          :invalid-secret ""
          :kasittely-maksamatta "Sinun tulee maksaa käsittelymaksu, ennen kuin hakemuksesi otetaan käsittelyyn. Huomaathan että sinun tulee myös maksaa erikseen päätösmaksu ennen kuin saat päätöksen."
          :kasittely-maksettu "Käsittelymaksusi on maksettu onnistuneesti. Tapahtumasta on lähetetty sinulle myös vahvistus sähköpostiin. Huomaathan että sinun tulee myös maksaa erikseen päätösmaksu ennen kuin saat päätöksen. Saat sähköpostiin ilmoituksen kun hakemuksesi on käsitelty."
          :paatos-maksamatta "Hakemuksesi on nyt käsitelty. Siirry maksamaan päätösmaksu."
          :paatos-maksettu "Hakemuksesi päätösmaksu on maksettu. Tapahtumasta on lähetetty sinulle myös vahvistus sähköpostiin.")]

      [:div (stylefy/use-style lasku-container-style)
        (when kasittely
          [invoice-item "Käsittelymaksu" kasittely])

        (when paatos
          [invoice-item "Päätösmaksu" paatos])]

      (if (some? pay-id)
        [:span (use-style button-style {:on-click #(on-maksa-click pay-id)})
          "Siirry maksamaan"
          [:span (use-style {:margin-left "7px"
                             :vertical-align "middle"}) [icon/trending_flat]]]
        [:div (use-style {:margin-top "30px"})])

     (when @payment-form
           [paytrail-payment-form @payment-form])

     ]))

(defn lasku-container []
  (let [;hakukohteet-label (subscribe [:translation :hakukohderyhma/hakukohteet])
        invoices (subscribe [maksut-subs/maksut-invoice])]
    (fn []
      (let []

        [:<>
         [h/heading {:cypressid (str "sub-heading")
                     :level     :h2}
                    "Tutkinnon tunnustaminen"]

         ;[:span (use-style {:grid-row 1 :grid-column "1 / 3"}) @hakukohteet-label]

         [laskut-container @invoices]



         ]))))

(defn tutu-maksut-panel []
  (let [fullname (subscribe [maksut-subs/maksut-invoice-fullname])]
    [p/panel
      {:cypressid "tutu-maksut-panel"}
      "Maksutapahtumat"
      @fullname
      [:div (use-style grid-styles)
        [lasku-container]]]))
