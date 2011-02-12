(ns cljs-server.deploy
  (:use [org.jclouds.compute]
        [clj-ssh.ssh]
        [clojure.java.shell])
  (:require [clojure.string :as string]
            [clojure.contrib.logging :as logging]))

(deftype SshLogger
   [log-level]
   com.jcraft.jsch.Logger
   (isEnabled
    [_ level]
    (>= level log-level))
   (log
    [_ level message]
    (logging/log (ssh-log-levels level) message nil "clj-ssh.ssh")))

(com.jcraft.jsch.JSch/setLogger (SshLogger. com.jcraft.jsch.Logger/FATAL))

(def amazon-key (string/trim (slurp "/Users/zkim/.amazon-key")))
(def amazon-secret (string/trim (slurp "/Users/zkim/.amazon-secret")))

(def node-name "cljsserver")

(defn compute [] (compute-service "ec2" amazon-key amazon-secret :ssh))

(defn cljs-server-node [] (build-template (compute) {:image-id "us-east-1/ami-08728661" :min-ram 512
                                           :authorize-public-key (slurp (str (. System getProperty "user.home") "/.ssh/id_rsa.pub"))
                                           :inbound-ports [22 80 81 443 8080 8081]}))

(defn print-node [node]
  (println "\t" (tag node) " " (id node) " " (seq (public-ips node)) " " (seq (private-ips node)) " " (when (running? node) "RUNNING") ))

(defn list-nodes [& [t]]
  (with-compute-service [(compute)]
    (println "Nodes:")
    (doseq [n (if t (filter #(= t (tag %)) (nodes)) (nodes))]
      (print-node n))
    (println)))

(defn sn [req cmd & opts]
  (let [opts (apply hash-map opts)
        cmd (if (:dir opts)
              (str "cd " (:dir opts) " && " cmd)
              cmd)
        cmd (string/replace cmd "'" "\\'")
        cmd (if (:sudo opts)
              (str "sudo -i '" cmd "'")
              cmd)]
    (with-ssh-agent []
      (add-identity "/Users/zkim/.ssh/id_rsa")
      (let [session (session (first (public-ips (:node req))) :username "ec2-user" :strict-host-key-checking :no)]
        (with-connection session
          (let [result (ssh-shell session cmd :bytes {:pty true})
                res-map {:cmd cmd
                         :code (first result)
                         :out (String. (second result))}]
            (when (:post req)
              ((:post req) res-map))
            (assoc req :results (conj (:results req) res-map))))))))

(defn push-file-chmod [req local remote perms]
  (with-ssh-agent []
      (add-identity "/Users/zkim/.ssh/id_rsa")
      (let [session (session (first (public-ips (:node req))) :username "ec2-user" :strict-host-key-checking :no)]
        (with-connection session
          (sftp session :put local remote))))
  (sn req (str "chmod " (name perms) " " remote)))

#_(def n (with-compute-service [(compute)] (first (filter running? (filter #(= node-name (tag %)) (nodes))))))

(defn install-git [req]
  (-> req
      (sn "yum -y install git" :sudo true)))

(defn install-java [req]
  (-> req
      (sn "yum -y install java-1.6.0-openjdk" :sudo true)
      (sn "yum -y install java-1.6.0-openjdk-devel" :sudo true)))

(def cake-bin "/home/ec2-user/cake/bin/cake")
(def lein-bin "/home/ec2-user/lein")

(defn install-cake [req]
  (-> req
      (sn "rm -rf ./cake")
      (sn "git clone git://github.com/ninjudd/cake.git")
      (sn (str cake-bin " install") :dir "./cake")))

(defn install-lein [req]
  (-> req
      (sn "wget https://github.com/technomancy/leiningen/raw/stable/bin/lein --no-check-certificate")
      (sn "chmod a+x ./lein")))

(defn install-cljs-server [req]
  (-> req
      (sn "rm -rf ./cljs-server")
      (sn "git clone git@github.com:zkim/cljs-server.git")))

(defn install-nsfw [req]
  (-> req
      (sn "rm -rf ./nsfw")
      (sn "git clone git@github.com:zkim/nsfw.git")
      (sn (str lein-bin " deps") :dir "./nsfw")
      (sn (str lein-bin " install") :dir "./nsfw")))

(defn install-cljs [req]
  (-> req
      (sn "rm -rf ./cljs")
      (sn "git clone git@github.com:zkim/cljs.git")
      (sn (str lein-bin " deps") :dir "./cljs")
      (sn (str lein-bin " install") :dir "./cljs")))

(defn install-from-git [req name]
  (-> req
      (sn "rm -rf ./")
      (sn "git clone git@github.com:zkim/.git")
      (sn (str lein-bin " deps") :dir "./")
      (sn (str lein-bin " install") :dir "./")))

(defn install-mongo [req]
  (-> req
      (sn "killall mongod" :sudo true)
      (sn "rm -rf /usr/share/mongodb" :sudo true)
      (sn "wget http://fastdl.mongodb.org/linux/mongodb-linux-i686-1.6.4.tgz" :sudo true)
      (sn "tar -xvf mongodb-linux-i686-1.6.4.tgz" :sudo true)
      (sn "mv mongodb-linux-i686-1.6.4 mongodb" :sudo true)
      (sn "mv mongodb /usr/share" :sudo true)
      (sn "mkdir /var/mongodata" :sudo true)
      (sn "/usr/share/mongodb/bin/mongod --dbpath /var/mongodata &" :sudo true)))

(defn deploy-cljs-server [req]
  (-> req
      (sn "killall java")
      (sn "git pull origin" :dir "./cljs-server")
      #_(sn (str lein-bin " clean") :dir "./cljs-server")
      #_(sn (str lein-bin " deps") :dir "./cljs-server")
      (sn "sh ./run-server" :dir "./cljs-server")))

(defn push-creds [req]
  (-> req
      (push-file-chmod "./resources/pallet/github_deploy_key" "./.ssh/id_rsa" :0600)
      (push-file-chmod "./resources/pallet/github_deploy_key.pub" "./.ssh/id_rsa.pub" :0644)
      (push-file-chmod "./resources/pallet/github_known_hosts" "./.ssh/known_hosts" :0644)))

(defn bootstrap-node [n]
  (-> {:node n
       :post (fn [res]
               (if (= 0 (:code res))
                 (println "OK    .. " (:cmd res))
                 (do (println "ERROR .. " (:cmd res))
                     (println "\t" (:out res)))))}
      (push-creds)
      (install-git)
      (install-java)
      (install-lein)
      (install-nsfw)
      (install-cljs)
      (install-cljs-server)
      (install-mongo)
      (deploy-cljs-server)))

#_(deploy-beeronsale {:node (last (nodes))
       :post (fn [res]
               (if (= 0 (:code res))
                 (println "OK    .. " (:cmd res))
                 (do (println "ERROR .. " (:cmd res))
                     (println "\t" (:out res)))))})

(defn start-nodes [t c]
  (with-compute-service [(compute)]
    (time (run-nodes t c (cljs-server-node)))
    (println "Bootstrapping")
    (doseq [n (filter running? (nodes-with-tag t))]
      (bootstrap-node n))
    (println "Done provisioning.")))

(defn stop-nodes [t c]
  (with-compute-service [(compute)]
    (doseq [id (take c (map id (filter running? (nodes-with-tag t))))]
      (destroy-node id)
      (println "Done stopping."))))


(defn provision-nodes [t c]
  (try
    (with-compute-service [(compute)]
      (let [nodes (filter #(and (running? %) (= t (tag %))) (nodes))
            diff (- c (count nodes))]
        (cond
         (> diff 0) (do (println "Provisioning" diff t "nodes")
                        (start-nodes t diff))
         (< diff 0) (do (println "Stopping" (Math/abs diff)  t "nodes")
                        (stop-nodes t (Math/abs diff)))
         :else (println c t "nodes already running. Exiting.")))
      (list-nodes t))
    (catch org.jclouds.aws.AWSResponseException e
      (println "EXCEPTION:" (.getMessage (.getError (.getMessage e)))))))

(defn provision [num]
  (let [tag node-name
        count num]
    (if (> count 3)
      (println "Can't provision more than 3 nodes")
      (provision-nodes tag count))))

(defn push-local-mongo [req]
  (sh "rm" "-rf" "/tmp/beeronsale*")
  (sh "mongodump" "--db" "beeronsale" "-o" "/tmp")
  (sh "tar" "cvf" "beeronsale.mongo.bkp" "beeronsale" :dir "/tmp")
  (sh "gzip" "beeronsale.mongo.bkp" :dir "/tmp")
  (-> req
      (sn "rm -rf /tmp/beeronsale*")
      (push-file-chmod "/tmp/beeronsale.mongo.bkp.gz" "/tmp/beeronsale.mongo.bkp.gz" :0777)
      (sn "tar -xvf beeronsale.mongo.bkp.gz" :dir "/tmp")
      (sn "/usr/share/mongodb/bin/mongorestore /tmp/beeronsale")
      (sn "rm -rf /tmp/beeronsale*"))
  (sh "rm" "-rf" "/tmp/beeronsale")
  (sh "rm" "-rf" "/tmp/beeronsale.mongo.bkp")
  (sh "rm" "-rf" "/tmp/beeronsale.mongo.bkp.gz"))

(defn pull-remote-mongo [req]
  (-> req
      (sn "rm -rf /tmp/beeronsale*")
      (sn "/usr/share/mongodb/bin/mongodump --db beeronsale -o /tmp")
      (sn "tar cvf beeronsale.mongo.bkp beeronsale" :dir "/tmp")
      (sn "gzip beeronsale.mongo.bkp" :dir "/tmp"))
  (sh "rm" "-rf" "/tmp/beeronsale*")
  (sh "scp" (str "ec2-user@" (first (public-ips (:node req))) ":/tmp/beeronsale.mongo.bkp.gz") "/tmp/beeronsale.mongo.bkp.gz")
  (sh "tar" "-xvf" "beeronsale.mongo.bkp.gz" :dir "/tmp")
  (sh "mongorestore" "/tmp/beeronsale"))

(defn my-nodes []
  (with-compute-service [(compute)]
    (filter running? (nodes-with-tag node-name))))

(defn help [])

