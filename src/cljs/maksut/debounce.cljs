(ns maksut.debounce)

(defn debounce [f timeout]
  (let [id (atom nil)]
    (fn [& args]
      (when (not (nil? @id))
        (js/clearTimeout @id))
      (reset! id (js/setTimeout
                   (apply partial f args)
                   timeout)))))
