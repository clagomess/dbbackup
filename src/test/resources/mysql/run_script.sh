#! /bin/bash

mysql -u root -p010203 < config.sql
mysql -u root -p010203 dbbackup < create.sql
mysql -u root -p010203 dbbackup < insert.sql