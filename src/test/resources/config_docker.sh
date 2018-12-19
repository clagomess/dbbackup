#! /bin/bash

# ORACLE
echo "CONFIG ORACLE"
sqlplus sys/Oradoc_db1@ORCLCDB as sysdba <<EOL
@oracle/config.sql
alter session set CURRENT_SCHEMA=DBBACKUP;
@oracle/create.sql
@oracle/insert.sql
commit;
exit
EOL