create table tbl_dbbackup (
  field_null text,
  field_text text,
  field_integer integer,
  field_numeric numeric,
  field_real real,
  field_blob blob
);

-- TABELAS PARA TESTE DE MIGRAÇÃO
create table tbl_dbbackup_mysql (
  field_null text,
  field_int integer,
  field_bigint integer,
  field_decimal numeric,
  field_tinyint integer,
  field_datetime text,
  field_date text,
  field_blob blob,
  field_longblob blob,
  field_longtext text,
  field_varchar text,
  field_text text,
  field_time text
);

create table tbl_dbbackup_oracle (
  field_null text,
  field_number integer,
  field_number_precision numeric,
  field_date text,
  field_blob blob,
  field_clob text,
  field_varchar2 text
);

create table tbl_dbbackup_postgresql (
  field_null text,
  field_float8 numeric,
  field_numeric integer,
  field_int4 integer,
  field_int8 integer,
  field_int2 integer,
  field_timestamptz text,
  field_timestamp text,
  field_date text,
  field_text text,
  field_varchar text,
  field_bytea blob,
  field_time text,
  field_boolean integer
);

create table tbl_dbbackup_h2 (
  field_null text,
  field_bigint integer,
  field_numeric numeric,
  field_decimal numeric,
  field_integer integer,
  field_float numeric,
  field_real numeric,
  field_double numeric,
  field_smallint integer,
  field_tinyint integer,
  field_timestamp text,
  field_time text,
  field_date text,
  field_blob blob,
  field_clob text,
  field_varchar text,
  field_varchar_ignorecase text,
  field_boolean integer
);
