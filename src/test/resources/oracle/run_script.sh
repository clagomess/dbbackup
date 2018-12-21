#! /bin/bash

sqlplus sys/Oradoc_db1@ORCLCDB as sysdba <<EOL
@config.sql
alter session set CURRENT_SCHEMA=DBBACKUP;
@create.sql
@insert.sql
commit;
exit
EOL