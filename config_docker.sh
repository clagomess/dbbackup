#!/usr/bin/env bash

echo "ORACLE DOCKER"
docker exec dbbackup_oracle sh -c "source /home/oracle/.bashrc; sqlplus sys/Oradoc_db1 as sysdba <<EOL
@config.sql
alter session set CURRENT_SCHEMA=DBBACKUP;
@create.sql
@insert.sql
commit;
exit
EOL"

echo "POSTGRESQL DOCKER"
docker exec dbbackup_postgresql sh -c "psql -U postgres -a -f /tmp/dbbackup/create.sql"
docker exec dbbackup_postgresql sh -c "psql -U postgres -a -f /tmp/dbbackup/insert.sql"

echo "MARIADB DOCKER"
docker exec dbbackup_mariadb sh -c "mysql -u root -p010203 < /tmp/dbbackup/config.sql"
docker exec dbbackup_mariadb sh -c "mysql -u root -p010203 dbbackup < /tmp/dbbackup/create.sql"
docker exec dbbackup_mariadb sh -c "mysql -u root -p010203 dbbackup < /tmp/dbbackup/insert.sql"
