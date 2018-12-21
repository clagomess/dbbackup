#!/usr/bin/env bash

echo "ORACLE DOCKER"
docker stop dbbackup_oracle
docker rm dbbackup_oracle

echo "POSTGRESQL DOCKER"
docker stop dbbackup_postgresql
docker rm dbbackup_postgresql

echo "MARIADB DOCKER"
docker stop dbbackup_mysql
docker rm dbbackup_mysql