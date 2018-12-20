#!/usr/bin/env bash

echo "ORACLE DOCKER"
docker build -t dbbackup_oracle src/test/resources/oracle
docker run -d --name dbbackup_oracle --net=host dbbackup_oracle:latest