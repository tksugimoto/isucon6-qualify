#!/bin/sh -xe

source ./env

echo '1: deploy ======================================='
pushd webapp/java
  echo '1.1: clean ======================================='
  pushd isuda
    mvn clean
  popd
  pushd isutar
    mvn clean
  popd
popd

echo '2: upload ======================================='
ssh ${USER}@${SERVER} 'rm -rf /home/isucon/webapp/java'
tar cvzf java.tgz webapp/java
scp -r java.tgz ${USER}@${SERVER}:/home/isucon/webapp/
ssh ${USER}@${SERVER} 'tar xvzf /home/isucon/webapp/java.tgz'
ssh ${USER}@${SERVER} 'rm /home/isucon/webapp/java.tgz'

echo '3: build ======================================='
ssh ${USER}@${SERVER} 'cd webapp/java/isuda  && ./mvnw package && chmod 544 target/isuda-*.jar'
ssh ${USER}@${SERVER} 'cd webapp/java/isutar && ./mvnw package && chmod 544 target/isutar-*.jar'

echo '4: service definition update ======================================='
pushd provisioning/image/files
  scp isuda.java.service ${USER}@${SERVER}:/home/isucon
  ssh ${USER}@${SERVER} sudo mv /home/isucon/isuda.java.service /etc/systemd/system/
  scp isutar.java.service ${USER}@${SERVER}:/home/isucon
  ssh ${USER}@${SERVER} sudo mv /home/isucon/isutar.java.service /etc/systemd/system/
popd

echo 'restart service ======================================='
ssh ${USER}@${SERVER} sudo systemctl daemon-reload
ssh ${USER}@${SERVER} sudo systemctl restart isuda.java.service
ssh ${USER}@${SERVER} sudo systemctl restart isutar.java.service
