(ns maksut.util.date
  (:import (java.time LocalDate ZoneId)))

(defonce helsinki-zone ^ZoneId (ZoneId/of "Europe/Helsinki"))

(defn plus-days-from-now ^LocalDate [days]
  (.plusDays (LocalDate/now) days))