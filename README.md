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

### Docker Mount:
- Logar no DockerHub com permissão ao conteiner "Oracle Database Enterprise Edition" : `docker login`
- Baixar imagem: `docker pull store/oracle/database-enterprise:12.2.0.1-slim`
- Build: `docker build -t dbbackup .`
- Run: `docker run -d --name dbbackup -p 1521:1521 --net=host dbbackup:latest`
- Finalizar: `docker stop dbbackup`

### Docker Utils
- Remover lixo: `docker container rm $(docker container ls -a -q)`
- Obter bash: `docker exec -i -t dbbackup /bin/bash`
- Monitorar Log: `docker attach dbbackup`