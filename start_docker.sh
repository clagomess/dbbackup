#!/usr/bin/env bash

echo "ORACLE DOCKER"
docker build -t dbbackup_oracle src/test/resources/oracle
docker run -d --name dbbackup_oracle -p 1521:1521 dbbackup_oracle:latest

echo "POSTGRESQL DOCKER"
docker build -t dbbackup_postgresql src/test/resources/postgresql
docker run -d --name dbbackup_postgresql -p 5432:5432 dbbackup_postgresql:latest

echo "MARIADB DOCKER"
docker build -t dbbackup_mysql src/test/resources/mysql
docker run -d --name dbbackup_mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=010203 dbbackup_mysql:latest
