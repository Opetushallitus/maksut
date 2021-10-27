(ns maksut.styles.styles-colors)

(def black "#2A2A2A")

(def blue "#0A789C")
(def blue-lighten-1 "#159ECB")
(def blue-lighten-2 "#00B5F0")
(def blue-lighten-3 "#A6DCEE")

(def gray "#666666")
(def gray-lighten-1 "#666666")
(def gray-lighten-3 "#CCCCCC")
(def gray-lighten-5 "#F5F5F5")
(def gray-lighten-6 "#FAFAFA")

;Tutu maksut näkymän yllä olevat pallurat
(def process-circle-border "#aeaeae")
(def process-circle-border-selected "#007373")
(def process-circle-bg-selected "#007373")
(def process-circle-bg "#ffffff")
(def process-circle-fg "#101010")
(def process-circle-fg-selected "#ffffff")
(def process-circle-text "#353535")
(def process-circle-text-selected "#006969")


(def invoice-bg "#f5f7f9")

(def white "#FFFFFF")

(def red "#FF0000")
(def red-dark-1 "#e85454")
(def red-dark-2 "#e35b5b")

(def invoice-status {
    :active  { :background-color "#f9e39f"
               :dot "#de9327"
               :text "#612d00"}
    :paid    { :background-color "#e2fae4"
               :dot "#61a33b"
               :text "#237a00"}
    :overdue { :background-color "#ff543b"
               :dot "#c72828"
               :text "#f8f8f8"}})