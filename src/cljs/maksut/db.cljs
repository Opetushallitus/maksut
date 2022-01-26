(ns maksut.db
  (:require [maksut.i18n.translations :as translations]
            [maksut.routes :as routes]))

(def default-db
  {:active-panel                      {:panel      routes/default-panel
                                       :parameters {:path  {}
                                                    :query {}}}
   :alert                             {:message ""
                                       :id      nil}
   :requests                          #{}
   :lang                              :fi
   :translations                      translations/local-translations
   :maksut                            {:invoice                  nil
                                       :payment-form             nil
                                       :secret                   nil}})
