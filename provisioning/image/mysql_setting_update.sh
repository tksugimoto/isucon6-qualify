#!/bin/bash

set -ex
cd $(dirname $0)


sudo install \
     --owner=root --mode=644 \
     ./files/my.cnf \
     /etc/mysql/my.cnf

sudo systemctl restart mysql.service
