;; clj-logging-config - Logging configuration for Clojure.

;; by Malcolm Sparks

;; Copyright (c) Malcolm Sparks. All rights reserved.

;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can
;; be found in the file epl-v10.html at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the terms of
;; this license.  You must not remove this notice, or any other, from this
;; software.

(require '[clojure.java.io :as io])
(import (org.apache.log4j PatternLayout FileAppender))
(use 'clojure.tools.logging 'clj-logging-config.log4j)

;; Try these examples in a REPL. Exercise each form in turn to see what is happening.
;; By default messages will go to standard out, not the REPL - so check your console.

(set-logger!)
(info "Just a plain logging message, you should see the level at the beginning")

(set-logger! :pattern "%m%n")
(info "A plain logging message, nothing added")

(set-logger! :pattern "[%p] %c - %m%n")
(info "<- A logging message with the priority and category prepended")

(set-logger! :pattern "%d - [%p] %c - %m%n")
(info "A logging message with the date in front")

(set-logger! :level :debug)
(debug "A debug message")
(info "Some info message")

(set-logger! :level :warn)
(info "You won't see me")
(warn "But you'll see me!")

(set-logger! :level :debug)
(debug "Now you can see a debug message")

;; If you get stuck you can reset the logging system with this :-
(reset-logging!)

;; If you want to see what is going on, try setting the clj-logging-config's own
;; config logging to debug :-
(set-config-logging-level! :debug)

;; (Now go back to the beginning and repeat with the config logging set to debug)

;; We can set multiple the loggers simultaneously, in a defined order.
(set-loggers! 
 :root {:out :console}
 :config {:level :info}
 "user" {:level :info})

(info "Test logger")

;; (with-logging-config) allows you to overlay a temporary thread-local specific logging configuration.
;; One example is if you are writing a batch system and want a log file per job.

(with-open [f (io/output-stream (io/file "job-123.log"))]
  (with-logging-config [:root {:level :debug :out f :pattern ">>> %d - %m %n"}]
    (logf :info "foo")))

(with-logging-config
  [*ns* {:level :debug :out :console :pattern "%d %m (%x) %n"}
   :config {:level :info}]
  (with-logging-context "jobid=56"
    (with-logging-context "part=A"
      (info "Here's some logging inside some context"))))

(with-logging-config
  [:root {:level :info :out :console :pattern "%m customer=%X{customer} job=%X{job-id} %n"}
   :config {:level :info}]
  (with-logging-context  {:job-id 1234
                          :parent-id 56}
    (with-logging-context {:customer "Fred"}
      (info "Here's some logging inside some MDC context"))))

;; Now you've set the config logging level, go back to the beginning and
;; re-evaluate each form.

;; Sometimes printing out the current configuration can help diagnose problems :-
;; (pprint (get-logging-config))

;; TODO: Test agents, particular check whether MDCs are propagated to 'child'
;; threads as claimed by
;; file:///home/malcolm/Downloads/apache-log4j-1.2.16/site/apidocs/org/apache/log4j/MDC.html


