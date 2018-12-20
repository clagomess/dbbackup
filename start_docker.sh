#!/usr/bin/env bash

echo "ORACLE DOCKER"
docker build -t dbbackup_oracle src/test/resources/oracle
docker run -d --name dbbackup_oracle --net=host dbbackup_oracle:latest

echo "POSTGRESQL DOCKER"
docker build -t dbbackup_postgresql src/test/resources/postgresql
docker run -d --name dbbackup_postgresql --net=host dbbackup_postgresql:latest