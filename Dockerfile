FROM store/oracle/database-enterprise:12.2.0.1-slim

WORKDIR /home/oracle/dbbackup
ADD src/test/resources /home/oracle/dbbackup

EXPOSE 1521