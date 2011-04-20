
(def mongo-name "mongodb-linux-i686-1.8.1")
(def mongo-tar (str mongo-name ".tgz"))
(def mongo-url (str "http://fastdl.mongodb.org/linux/" mongo-tar))
(def mongo-bin (str "/usr/share/" mongo-name "/bin"))
(def mongo-data "/var/db/mongodata")

(defn link-mongo [& names]
  (doseq [n names]
    (rm-f (str "/usr/bin/" (as-str n)))
    ($ "ln -s " mongo-bin "/" n " /usr/bin/" n)))

(def mongod-local
  (str "#!/bin/bash
mongod --dbpath " mongo-data))

(def mongod-repair
  (str "#!/bin/bash
rm -f " mongo-data "/mongod.lock
mongod --dbpath " mongo-data " --repair"))

(sudo
 (wget mongo-url)
 (untar mongo-tar)
 (rm-rf mongo-tar)
 (mv mongo-name "/usr/share")
 (rm-rf mongo-name)
 (mkdir-p mongo-data)
 (chmod :0777 mongo-data)
 (spit (str "/usr/share/" mongo-name "/bin/mongod-local") mongod-local :0755)
 (spit (str "/usr/share/" mongo-name "/bin/mongod-repair") mongod-repair :0755)
 (link-mongo :mongo :mongod :mongod-local :mongod-repair))



