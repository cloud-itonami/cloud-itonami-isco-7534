(ns upholcoord.actor-test
  (:require [clojure.test :refer [deftest is testing]]
            [upholcoord.actor :as actor]
            [upholcoord.store :as store]))

(defn- fresh-store []
  (let [st (store/mem-store)]
    (store/register-upholsterer! st {:upholsterer-id "upholsterer-1" :name "Kobo Tanaka"})
    (store/register-workshop! st {:workshop-id "W-1" :name "Kobo Upholstery Workshop" :max-supply-cost 2000})
    st))

(deftest commits-a-registered-work-log
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:upholsterer-id "upholsterer-1" :op :log-work-record :stake :low
                  :workshop-id "W-1" :task "commission progress log"}
        result (actor/run-request! graph request {} "thread-1")]
    (is (= :done (:status result)))
    (is (some? (get-in result [:state :record])))
    (is (= 1 (count (store/records-of st "upholsterer-1"))))))

(deftest holds-an-unregistered-workshop-proposal
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:upholsterer-id "upholsterer-1" :op :log-work-record :stake :low
                  :workshop-id "W-ghost" :task "commission progress log"}
        result (actor/run-request! graph request {} "thread-2")]
    (is (= :hold (:disposition (:state result))))
    (is (empty? (store/records-of st "upholsterer-1")))))

(deftest interrupts-then-approves-safety-concern-on-human-approval
  (let [st (fresh-store)
        graph (actor/build-graph {:store st})
        request {:upholsterer-id "upholsterer-1" :op :flag-safety-concern :stake :low
                  :workshop-id "W-1" :hazard-type :hand-tool-hazard}
        interrupted (actor/run-request! graph request {} "thread-3")]
    (is (= :interrupted (:status interrupted)))
    (is (empty? (store/records-of st "upholsterer-1")))
    (let [resumed (actor/approve! graph "thread-3")]
      (is (= :done (:status resumed)))
      (is (= 1 (count (store/records-of st "upholsterer-1")))))))

(deftest holds-a-scope-excluded-op-even-at-high-confidence
  (testing "an actor run can never commit a proposal that would finalize an upholstery-execution decision, regardless of disposition path"
    (let [st (fresh-store)
          graph (actor/build-graph {:store st})
          request {:upholsterer-id "upholsterer-1" :op :finalize-upholstery-operation :stake :low
                    :workshop-id "W-1" :task "finish decision"}
          result (actor/run-request! graph request {} "thread-4")]
      (is (= :done (:status result)))
      (is (= :hold (:disposition (:state result))))
      (is (empty? (store/records-of st "upholsterer-1"))))))

(deftest holds-a-workshop-safety-clearance-op-even-at-high-confidence
  (testing "an actor run can never commit a proposal that would declare a workshop safety-cleared, regardless of disposition path"
    (let [st (fresh-store)
          graph (actor/build-graph {:store st})
          request {:upholsterer-id "upholsterer-1" :op :declare-workshop-safety-cleared :stake :low
                    :workshop-id "W-1" :task "safety clearance"}
          result (actor/run-request! graph request {} "thread-5")]
      (is (= :done (:status result)))
      (is (= :hold (:disposition (:state result))))
      (is (empty? (store/records-of st "upholsterer-1"))))))
