#!/bin/bash

set -ex
cd $(dirname $0)

myuser=root
mypass=root

isuda_mydb=isuda
isutar_mydb=isutar

# Isuda inxex作成
mysql -u${myuser} -p${mypass} ${isuda_mydb} < ../../db/isuda_add_index.sql

# Isutar index作成
mysql -u${myuser} -p${mypass} ${isutar_mydb} < ../../db/isutar_add_index.sql
