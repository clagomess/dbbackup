create table tbl_dbbackup (
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

-- TABELAS PARA TESTE DE MIGRAÇÃO
create table tbl_dbbackup_oracle (
  field_null date,
  field_number bigint,
  field_number_precision decimal(10,2),
  field_date datetime,
  field_blob longblob,
  field_clob longtext,
  field_varchar2 varchar(200)
);

create table tbl_dbbackup_postgresql (
  field_null date,
  field_float8 decimal(10,2),
  field_numeric bigint,
  field_int4 int,
  field_int8 int,
  field_int2 int,
  field_timestamptz datetime,
  field_timestamp datetime,
  field_date date,
  field_text longtext,
  field_varchar varchar(200),
  field_bytea longblob,
  field_time time,
  field_boolean tinyint(1)
);

create table tbl_dbbackup_h2 (
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
  field_clob longtext,
  field_varchar varchar(200),
  field_varchar_ignorecase varchar(200),
  field_boolean tinyint(1)
);

create table tbl_dbbackup_sqlite (
  field_null text,
  field_text text,
  field_integer bigint,
  field_numeric numeric,
  field_real real,
  field_blob blob
);

create table `Tbl_DBBackup_Case` (
    `Field_Text` text,
    `Field_Integer` bigint
);
