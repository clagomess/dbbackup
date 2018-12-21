# dbbackup

### Uso:
Bash Ex.: `java -jar dbbackup.jar -db MYSQL -ope GET -url jdbc:mysql://localhost/database -user root -pass 010203 -schema dbbackup`

CLI DOC:
```
usage: dbbackup
 -db <arg>            {ORACLE, MYSQL, POSTGRESQL}
 -dump_format <arg>   Formato de saída do dump. Ideal para migração. Ex.:
                      {ORACLE, MYSQL, POSTGRESQL}
 -lob <arg>           {1} - Importar/Exportar LOB
 -ope <arg>           {GET, PUT}
 -pass <arg>          pass
 -schema <arg>        schema
 -schema_exp <arg>    Nome do schema para exportação
 -table <arg>         Tabela(s) a ser exportadas. Ex.: -table foo -table
                      bar ...
 -url <arg>           jdbc:oracle:thin:@localhost:1521/XE -
                      jdbc:mysql://localhost/database -
                      jdbc:postgresql://localhost:5432/postgres
 -user <arg>          user
 -workdir <arg>       Pasta de localização do dump. default: ./dump
```

### Conversão
| generic | Mysql | Postgresql | Oracle |
| --- | --- | --- | --- |
| `NUMBER` | <ul><li>int</li><li>bigint</li><li>decimal</li><li>tinyint</li></ul> | <ul><li>float8</li><li>numeric</li><li>int4</li><li>int8</li><li>int2</li></ul> | <ul><li>NUMBER</li></li> |
| `DATETIME` | <ul><li>datetime</li></li> | <ul><li>timestamptz</li><li>timestamp</li></ul>  | <ul><li>DATE</li></li> |
| `DATE` | <ul><li>date</li></li> | <ul><li>date</li></li> | <ul><li>DATE</li></li> |
| `TIME` | - | - | <ul><li>DATE</li></li> |
| `BLOB` | <ul><li>blob</li><li>longblob</li></ul> | <ul><li>bytea</li></li> | <ul><li>BLOB</li></li> |
| `CLOB` | <ul><li>longtext</li></li> |  <ul><li>text</li></li> | <ul><li>CLOB</li></li> |
| `VARCHAR` | <ul><li>varchar</li><li>text</li></ul> | <ul><li>varchar</li></li> | <ul><li>VARCHAR2</li></li> |

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