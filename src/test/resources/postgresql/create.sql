create table tbl_dbbackup (
  field_null date,
  field_float8 float8,
  field_numeric numeric,
  field_int4 int4,
  field_int8 int8,
  field_int2 int2,
  field_timestamptz timestamptz,
  field_timestamp timestamp,
  field_date date,
  field_text text,
  field_varchar varchar(200),
  field_bytea bytea,
  field_time time,
  field_boolean boolean
);

-- TABELAS PARA TESTE DE MIGRAÇÃO
create table tbl_dbbackup_oracle (
  field_null date,
  field_number bigint,
  field_number_precision numeric(10,2),
  field_date timestamp,
  field_blob bytea,
  field_clob text,
  field_varchar2 varchar(200)
);

create table tbl_dbbackup_mysql (
  field_null date,
  field_int numeric,
  field_bigint numeric,
  field_decimal numeric(10,2),
  field_tinyint numeric,
  field_datetime timestamp,
  field_date date,
  field_blob bytea,
  field_longblob bytea,
  field_longtext text,
  field_varchar varchar(200),
  field_text varchar(200),
  field_time time
);

create table tbl_dbbackup_h2 (
  field_null date,
  field_bigint bigint,
  field_numeric numeric,
  field_decimal decimal,
  field_integer integer,
  field_float float,
  field_real real,
  field_double decimal,
  field_smallint smallint,
  field_tinyint int,
  field_timestamp timestamp,
  field_time time,
  field_date date,
  field_blob bytea,
  field_clob text,
  field_varchar varchar(200),
  field_varchar_ignorecase varchar(200),
  field_boolean boolean
);

create table tbl_dbbackup_sqlite (
  field_null text,
  field_text text,
  field_integer bigint,
  field_numeric numeric,
  field_real numeric,
  field_blob bytea
);

create table "Tbl_DBBackup_Case" (
    "Field_Text" text,
    "Field_Integer" bigint
);