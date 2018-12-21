create table tbl_dbbackup (
  field_null date,
  field_bigint bigint,
  field_numeric numeric,
  field_decimal decimal,
  field_integer integer,
  field_float float,
  field_real real,
  field_double double,
  field_smallint smallint,
  field_tinyint tinyint,
  field_timestamp timestamp,
  field_time time,
  field_date date,
  field_blob blob,
  field_clob clob,
  field_varchar varchar,
  field_varchar_ignorecase varchar_ignorecase,
  field_boolean boolean,
);

-- TABELAS PARA TESTE DE MIGRAÇÃO
create table tbl_dbbackup_mysql (
  field_null date,
  field_int int,
  field_bigint bigint,
  field_decimal decimal(10,2),
  field_tinyint tinyint,
  field_datetime datetime,
  field_date date,
  field_blob blob,
  field_longblob longblob,
  field_longtext longtext,
  field_varchar varchar(200),
  field_text text,
  field_time time
);

create table tbl_dbbackup_oracle (
  field_null date,
  field_number number,
  field_number_precision number(10, 2),
  field_date date,
  field_blob blob,
  field_clob clob,
  field_varchar2 varchar2(200)
);

create table tbl_dbbackup_postgresql (
  field_null date,
  field_float8 float8,
  field_numeric numeric,
  field_int4 int4,
  field_int8 int8,
  field_int2 int2,
  field_timestamptz timestamp,
  field_timestamp timestamp,
  field_date date,
  field_text text,
  field_varchar varchar(200),
  field_bytea bytea,
  field_time time,
  field_boolean boolean
);