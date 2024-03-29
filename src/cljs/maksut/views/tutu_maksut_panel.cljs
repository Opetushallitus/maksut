(ns maksut.views.tutu-maksut-panel
  (:require [maksut.components.common.panel :as p]
            [maksut.components.common.headings :as h]
            [maksut.components.common.material-icons :as icon]
            [maksut.styles.styles-colors :as colors]
            [maksut.styles.styles-fonts :as vars]
            [maksut.styles.styles-init :refer [media-small]]
            [maksut.subs.maksut-subs :as maksut-subs]
            [maksut.dates.date-parser :refer [format-date]]
            [clojure.string]
            [re-frame.core :refer [subscribe]]
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

(def ^:private lasku-style
  {:border-radius "3px"
   :padding "20px"
   :min-width "320px"
   ::stylefy/media {media-small { :min-width "280px" }}
   :background-color colors/invoice-bg})

(def ^:private dot-style
  {:height "10px"
   :width "10px"
   :border-radius "50%"
   :display "inline-block"})

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
        status-text @(subscribe [:translation (keyword :maksu-tila status)])]
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
                         :color fg-color}]
     [:div (use-style dot-style)
      (if done
        [:span (use-style {:vertical-align "middle"}) [icon/done-bold]]
        (str index))]
    ))

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
                    ::stylefy/media {media-small {
                      :width "300px"
                      :grid-template-columns "150px 150px"}}
                    :grid-row "auto auto"
                    :grid-row-gap "10px"
                    })
     [circle-icon 1 true (= kasittely-status :paid)]
     [circle-icon 2 (or (= state :kasittely-maksettu) (= state :paatos-maksettu)) (= paatos-status :paid)]

   (case state
     :loading [:<>]
     :invalid-secret [:<>]
     :kasittely-maksamatta [:<>
       [:div (use-style header-active) @(subscribe [:translation :tutu-panel/tila-käsittelymaksu])]
       [:div (use-style header-passive) @(subscribe [:translation :tutu-panel/tila-käsittely])]]
     :kasittely-maksettu [:<>
       [:div (use-style header-passive) @(subscribe [:translation :tutu-panel/tila-käsittelymaksu])]
       [:div (use-style header-active) @(subscribe [:translation :tutu-panel/tila-käsittely])]]
     :paatos-maksamatta [:<>
       [:div (use-style header-active) @(subscribe [:translation :tutu-panel/tila-käsittely])]
       [:div (use-style header-passive) @(subscribe [:translation :tutu-panel/tila-päätösmaksu])]]
     :paatos-maksettu [:<>
       [:div (use-style header-passive) @(subscribe [:translation :tutu-panel/tila-käsittely])]
       [:div (use-style header-active) @(subscribe [:translation :tutu-panel/tila-päätösmaksu])]])

   ]))

(defn invoice-item [_ _]
  (let [value-style {:text-align "right"
                     :margin-bottom "10px"}
        separator-style {
                :grid-column "span 2"
                :border-bottom "1px solid #cecfd0"
                :margin-bottom "10px"
                :padding-bottom "-20px"
                }]
    (fn [header {:keys [order_id amount due_date status paid_at]} _]
      ^{:key (:order_id order_id)}
      [:div (use-style lasku-style)
       [:div (use-style {:margin-bottom "20px"
                         :text-align "center"})
        [:span (use-style (merge h/h3-styles value-style)) header]
       ]
       [:div (use-style { :display "grid"
                          :justify-content "space-between"
                          :grid-template-columns "1fr 1fr"})

        [:span @(subscribe [:translation :tutu-panel/maksu-tila])]
        [:div (use-style value-style) [invoice-status-indicator status]]

        [:span (use-style separator-style)]

        [:span @(subscribe [:translation :tutu-panel/maksu-summa])]
        [:span (use-style (merge h/h3-styles value-style {:padding-top "0px"})) (str amount "€")]

        [:span (use-style separator-style)]

        (case status
              :active
              [:<>
                [:span @(subscribe [:translation :tutu-panel/maksu-eräpäivä])]
                [:span (use-style value-style) (format-date due_date)]]
              :paid
              [:<>
                [:span @(subscribe [:translation :tutu-panel/maksu-maksupäivä])]
                [:span (use-style value-style) (format-date paid_at)]]
              :overdue
              [:<>
                [:span @(subscribe [:translation :tutu-panel/maksu-eräpäivä])]
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
        show-process (case state
                           :invalid-secret false
                           :loading false
                           true)
        state-text (if show-process
                     (case state
                       :kasittely-maksamatta [:<>
                                              @(subscribe [:translation :tutu-panel-ohje/kasittely-maksamatta-1])
                                              [:br] [:br]
                                              @(subscribe [:translation :tutu-panel-ohje/kasittely-maksamatta-2])
                                              [:br] [:br]
                                              @(subscribe [:translation :tutu-panel-ohje/kasittely-maksamatta-3])
                                              [:br] [:br]
                                              @(subscribe [:translation :tutu-panel-ohje/kasittely-maksamatta-4])]
                       :kasittely-maksettu [:<>
                                            @(subscribe [:translation :tutu-panel-ohje/kasittely-maksettu-1])
                                            [:br] [:br]
                                            @(subscribe [:translation :tutu-panel-ohje/kasittely-maksettu-2])
                                            [:br] [:br]
                                            @(subscribe [:translation :tutu-panel-ohje/kasittely-maksettu-3])]
                       @(subscribe [:translation (keyword :tutu-panel-ohje state)]))
                     "")
        pay-id (cond
                  (and (= state :kasittely-maksamatta) (= kasittely-status :active)) (:order_id kasittely)
                  (and (= state :paatos-maksamatta) (= paatos-status :active)) (:order_id paatos))
        show-process (case state
                           :invalid-secret false
                           :loading false
                           true)
        payment-form (subscribe [maksut-subs/maksut-payment-form])
        secret (subscribe [maksut-subs/maksut-secret])
        lang (subscribe [:lang])
        lasku-container-style {:display "flex"
                               :grid-gap "10px"
                               :flex-wrap "wrap"
                               :justify-content "center" }
        button-style {:margin-top "20px"
                      :margin-bottom "30px"
                      :background-color "#3A7A10"
                      ::stylefy/mode {:hover {:background-color "#254905"}} ; Green-900
                      :padding "10px 25px 10px 25px"
                      :cursor "pointer"
                      :color "#ffffff"}]
    [:<>
     (when show-process [process-map state kasittely-status paatos-status])
      [:div (use-style {:margin-top "10px"})]
      [h/heading {:level :text}
         state-text]
      (when (or (= state :kasittely-maksettu) (= state :paatos-maksettu))
            [h/heading {:level :text}
             @(subscribe [:translation :tutu-panel-ohje/yhteiskaytto-ohje])])
      [:div (use-style {:margin-bottom "10px"})]

      [:div (stylefy/use-style lasku-container-style)
        (when kasittely
          [invoice-item @(subscribe [:translation :tutu-panel/maksulaatikko-otsikko-käsittely]) kasittely])

        (when paatos
          [invoice-item @(subscribe [:translation :tutu-panel/maksulaatikko-otsikko-päätös]) paatos])]

      (if (some? pay-id)
        [:a (use-style button-style {:href (str "/maksut/api/lasku/" pay-id "/maksa?secret=" @secret "&locale=" (name @lang))})
          @(subscribe [:translation :tutu-panel/maksu-nappula])
          [:span (use-style {:margin-left "7px"
                             :vertical-align "middle"}) [icon/trending_flat]]]
        [:div (use-style {:margin-top "30px"})])

     (when @payment-form
           [paytrail-payment-form @payment-form])

     ]))


(defn error-container [error]
  (let [code (:code error)
        link    "https://www.oph.fi/fi/palvelut/tutkintojen-tunnustaminen"
        link-tag [:a {:href link} link]
        email   "recognition@oph.fi"
        email-tag [:a {:href (str "mailto:" email)} email]
        myynti-email "myyntilaskutus@oph.fi"
        myynti-email-tag [:a {:href (str "mailto:" myynti-email)} myynti-email]
        header (fn [header] [h/heading {:level :h2}
                            header])
        text-style {:color       colors/black
                    :font-size   "16px"
                    :font-weight vars/font-weight-regular
                    :line-height "24px"}
        text (fn [& text] (into [] (concat [:span (stylefy/use-style text-style)] text)))]
    (case code
          ("invoice-processing-oldsecret"
           "invoice-decision-oldsecret"
           "invoice-notfound-oldsecret")
          [:<>
           [header @(subscribe [:translation :invoice-not-found/paid-oldsecret-header])]]

          "invoice-processing-overdue"
          [:<>
           [header @(subscribe [:translation :invoice-not-found/processing-overdue-header])]
           [text
             @(subscribe [:translation :invoice-not-found/processing-overdue-text-1])
             [:br][:br]
             @(subscribe [:translation :invoice-not-found/processing-overdue-text-2]) email-tag "."
             [:br][:br]
             @(subscribe [:translation :invoice-not-found/processing-overdue-text-3]) link-tag]]

          "invoice-decision-overdue"
          [:<>
           [header @(subscribe [:translation :invoice-not-found/decision-overdue-header])]
           [text
            @(subscribe [:translation :invoice-not-found/decision-overdue-text-1])
            [:br][:br]
            @(subscribe [:translation :invoice-not-found/decision-overdue-text-2])
            [:br][:br]
            @(subscribe [:translation :invoice-not-found/decision-overdue-text-3]) myynti-email-tag "."
            [:br][:br]
            @(subscribe [:translation :invoice-not-found/decision-overdue-text-4]) email-tag "."]]

          ;:else "invoice-notfound-secret"
          [:<>
           [header @(subscribe [:translation :invoice-not-found/invalid-secret-header])]
           [text
             @(subscribe [:translation :invoice-not-found/invalid-secret-text])
             email-tag
             "." ]])))

(defn lasku-container []
  (let [aliotsikko (subscribe [:translation :tutu-panel/aliotsikko])
        invoices (subscribe [maksut-subs/maksut-invoice])]
    (fn []
        [:<>
         [h/heading {:level :h2}
                    @aliotsikko]

         [laskut-container @invoices]
         ])))

(defn tutu-maksut-panel []
  (let [fullname (subscribe [maksut-subs/maksut-invoice-fullname])
        error    @(subscribe [maksut-subs/maksut-invoice-error])]
    [p/panel
      {}
      (when-not error @(subscribe [:translation :tutu-panel/otsikko]))
      @fullname
      [:div (use-style grid-styles)
        (if error
          [error-container error]
          [lasku-container])]]))
