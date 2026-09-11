(ns upholcoord.store
  "SSoT for the ISCO-08 7534 upholstery workshop scheduling/logistics
  coordination actor (itonami actor pattern, ADR-2607121000 /
  CLAUDE.md Actors section; README's 'Robotics premise' — a workshop
  scheduling/logistics coordination robot performs crew scheduling,
  job/commission/progress-record logging and padding/fabric/frame-
  materials supply-order coordination for an upholstery workshop
  under this advisor/governor pair, which never dispatches hardware
  itself, never performs upholstery work itself, and never finalizes
  an upholstery-execution decision or a workshop-safety-clearance
  decision, nor overrides a shop safety officer's judgment — those
  remain the shop safety officer's exclusive judgment). ISCO-08 7534
  (Upholsterers and Related Workers) works with hand tools (staple
  guns, tack hammers, cutting knives) and handles padding/foam
  materials — a standard workshop hand-tool hazard — so this store's
  domain shape covers a job/commission record plus hand-tool-hazard/
  material-handling-hazard/equipment-condition safety reporting.
  Modeled closely on cloud-itonami-isco-7319's craftcoord.store
  (closest published, tested generic workshop scheduling/logistics
  coordination pattern).

  Domain:

    upholsterer — a registered upholstery workshop crew member
                  (:upholsterer-id, :name)
    workshop    — a registered upholstery workshop site {:workshop-id
                  :name :max-supply-cost}. `:max-supply-cost` is an
                  informational registered ceiling used only to decide
                  whether a `:coordinate-supply-order` proposal
                  escalates to human sign-off (the governor never
                  blocks a within-threshold order outright; it only
                  decides commit vs. escalate).
    record      — a committed operating record (a logged job/
                  commission/progress entry, a scheduled crew/task
                  operation, a flagged safety concern, or a
                  coordinated padding/fabric/frame-materials supply
                  order) — written ONLY via commit-record!.
    ledger      — append-only audit trail, commit or hold.")

(defprotocol Store
  (upholsterer [s upholsterer-id])
  (workshop [s workshop-id])
  (records-of [s upholsterer-id])
  (ledger [s])
  (register-upholsterer! [s u])
  (register-workshop! [s w])
  (commit-record! [s record])
  (append-ledger! [s fact]))

(defrecord MemStore [a]
  Store
  (upholsterer [_ upholsterer-id] (get-in @a [:upholsterers upholsterer-id]))
  (workshop [_ workshop-id] (get-in @a [:workshops workshop-id]))
  (records-of [_ upholsterer-id] (filter #(= upholsterer-id (:upholsterer-id %)) (:records @a)))
  (ledger [_] (:ledger @a))
  (register-upholsterer! [s m]
    (swap! a assoc-in [:upholsterers (:upholsterer-id m)] m) s)
  (register-workshop! [s w]
    (swap! a assoc-in [:workshops (:workshop-id w)] w) s)
  (commit-record! [s record]
    (swap! a update :records (fnil conj []) record) s)
  (append-ledger! [s fact]
    (swap! a update :ledger (fnil conj []) fact) s))

(defn mem-store
  ([] (mem-store {}))
  ([seed] (->MemStore (atom (merge {:upholsterers {} :workshops {} :records [] :ledger []}
                                    seed)))))
