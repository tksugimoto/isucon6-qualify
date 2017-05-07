#!/bin/bash

set -ex
cd $(dirname $0)

myuser=root
mypass=root

isutar_mydb=isutar

# Isutar index作成
mysql -u${myuser} -p${mypass} ${isutar_mydb} < ../../db/isutar_add_index.sql
