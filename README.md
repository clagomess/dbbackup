# dbbackup

### Uso:
```
usage: dbbackup
 -db <arg>            {ORACLE, MYSQL, POSTGRESQL}
 -dump_format <arg>   Formato de saida do dump. Ideal para migracao. Ex.:
                      {ORACLE, MYSQL, POSTGRESQL}
 -lob <arg>           {1} - Importar/Exportar LOB
 -ope <arg>           {GET, PUT}
 -pass <arg>          pass
 -schema <arg>        schema
 -schema_exp <arg>    Nome do schema para exportacao
 -url <arg>           jdbc:oracle:thin:@localhost:1521/XE -
                      jdbc:mysql://localhost/database -
                      jdbc:postgresql://localhost:5432/postgres
 -user <arg>          user
```