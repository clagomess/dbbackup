# dbbackup

### Uso:
Bash Ex.: 
```
java -jar dbbackup.jar -db MYSQL \
    -ope GET \
    -url jdbc:mysql://localhost/dbbackup \
    -user root \
    -pass 010203 \
    -schema dbbackup
```

CLI DOC:
```
usage: dbbackup
 -charset <arg>       Default: UTF-8. Ex.: UTF-8, ISO-8859-1 e US-ASCII
 -db <arg>            {ORACLE, MYSQL, MARIADB, POSTGRESQL, H2}
 -dump_format <arg>   Formato de saída do dump. Ideal para migração. Ex.:
                      {ORACLE, MYSQL, POSTGRESQL, H2}
 -lob <arg>           {1} - Importar/Exportar LOB
 -ope <arg>           {GET, PUT}
 -pass <arg>          pass
 -schema <arg>        schema
 -schema_exp <arg>    Nome do schema para exportação
 -table <arg>         Tabela(s) a ser exportada(s). Ex.: -table foo -table
                      bar ...
 -table_query <arg>   Query de consulta da(s) tabela(s) a ser
                      exportada(s). Ex.: -table_query "tbl_foo;select *
                      from tbl_foo" -table_query "tbl_bar;select * from
                      tbl_bar" ...
 -url <arg>           jdbc:oracle:thin:@localhost:1521/XE -
                      jdbc:mysql://localhost/database -
                      jdbc:mariadb://localhost/database -
                      jdbc:postgresql://localhost:5432/postgres -
                      jdbc:h2:./home/h2/dbbackup
 -user <arg>          user
 -workdir <arg>       Pasta de localização do dump. default: ./dump
```

### Conversão
| generic | Mysql/MariaDB | Postgresql | Oracle | H2 |
| --- | --- | --- | --- | --- |
| `NUMBER` | <ul><li>int</li><li>bigint</li><li>decimal</li><li>tinyint</li></ul> | <ul><li>float8</li><li>numeric</li><li>int4</li><li>int8</li><li>int2</li></ul> | <ul><li>NUMBER</li></li> | <ul><li>BIGINT</li><li>NUMERIC</li><li>DECIMAL</li><li>INTEGER</li><li>FLOAT</li><li>REAL</li><li>DOUBLE</li><li>SMALLINT</li></ul> |
| `DATETIME` | <ul><li>datetime</li></li> | <ul><li>timestamptz</li><li>timestamp</li></ul>  | <ul><li>DATE</li></li> | <ul><li>TIMESTAMP</li></li> |
| `DATE` | <ul><li>date</li></li> | <ul><li>date</li></li> | <ul><li>DATE</li></li> | <ul><li>DATE</li></li> |
| `TIME` | <ul><li>time</li></li> | <ul><li>time</li></li> | <ul><li>DATE</li></li> | <ul><li>TIME</li></li> |
| `BLOB` | <ul><li>blob</li><li>longblob</li></ul> | <ul><li>bytea</li></li> | <ul><li>BLOB</li></li> | <ul><li>BLOB</li></li> |
| `CLOB` | <ul><li>longtext</li></li> |  <ul><li>text</li></li> | <ul><li>CLOB</li></li> | <ul><li>CLOB</li></li> |
| `VARCHAR` | <ul><li>varchar</li><li>text</li></ul> | <ul><li>varchar</li></li> | <ul><li>VARCHAR2</li></li> | <ul><li>VARCHAR</li><li>VARCHAR_IGNORECASE</li></ul> |
| `BOOL` | <ul><li>tinyint</li></ul> | <ul><li>bool</li></li> | <ul><li>NUMBER</li></li> | <ul><li>BOOLEAN</li></li> |

### Docker Mount:
1. Logar no DockerHub com permissão ao contêiner "Oracle Database Enterprise Edition" : `docker login`
2. Baixar imagem Oracle: `docker pull store/oracle/database-enterprise:12.2.0.1-slim`
3. Iniciar contêiners: `start_docker.sh`
4. Rodar `run_script.sh` de cada contêiner
5. Rodar testes
6. Encerrar contêiners: `stop_docker.sh`

### Docker Utils
- Remover lixo: `docker container rm $(docker container ls -a -q)`
- Obter bash: `docker exec -i -t dbbackup /bin/bash`
- Monitorar Log: `docker attach dbbackup`