create table tbl_dbbackup (
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
  field_text text
);

-- TABELAS PARA TESTE DE MIGRAÇÃO
create table tbl_dbbackup_oracle (
  field_number bigint,
  field_number_precision decimal(10,2),
  field_date datetime,
  field_blob longblob,
  field_clob longtext,
  field_varchar2 varchar(200)
);

create table tbl_dbbackup_postgresql (
  field_float8 decimal(10,2),
  field_numeric bigint,
  field_int4 int,
  field_int8 int,
  field_int2 int,
  field_timestamptz datetime,
  field_timestamp datetime,
  field_date date,
  field_text longtext,
  field_varchar varchar(200)
);