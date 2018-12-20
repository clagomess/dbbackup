#! /bin/bash

psql -U postgres -a -f create.sql
psql -U postgres -a -f insert.sql