create table tbl_dbbackup (
  field_null date,
  field_number number(10),
  field_number_precision number(10, 2),
  field_date date,
  field_blob blob,
  field_clob clob,
  field_varchar2 varchar2(200),
  "Field_Case" varchar2(200)
);

-- TABELAS PARA TESTE DE MIGRAÇÃO
create table tbl_dbbackup_mysql (
  field_null date,
  field_int number(10),
  field_bigint number(10),
  field_decimal number(10,2),
  field_tinyint number(10),
  field_datetime date,
  field_date date,
  field_blob blob,
  field_longblob blob,
  field_longtext clob,
  field_varchar varchar2(200),
  field_text varchar2(200),
  field_time date,
  "Field_Case" varchar2(200)
);

create table tbl_dbbackup_postgresql (
  field_null date,
  field_float8 number(10,2),
  field_numeric number(10),
  field_int4 number(10),
  field_int8 number(10),
  field_int2 number(10),
  field_timestamptz date,
  field_timestamp date,
  field_date date,
  field_text clob,
  field_varchar varchar2(200),
  field_bytea blob,
  field_time date,
  field_boolean number(1),
  "Field_Case" varchar2(200)
);

create table tbl_dbbackup_h2 (
  field_null date,
  field_bigint number(10),
  field_numeric number(10),
  field_decimal number(10,2),
  field_integer number(10),
  field_float number(10,2),
  field_real number(10,2),
  field_double number(10,2),
  field_smallint number(10),
  field_tinyint number(10),
  field_timestamp date,
  field_time date,
  field_date date,
  field_blob blob,
  field_clob clob,
  field_varchar varchar2(200),
  field_varchar_ignorecase varchar2(200),
  field_boolean number(1),
  "Field_Case" varchar2(200)
);

create table tbl_dbbackup_sqlite (
  field_null varchar2(200),
  field_text varchar2(200),
  field_integer number(10),
  field_numeric number(10,2),
  field_real number(10,2),
  field_blob blob,
  "Field_Case" varchar2(200)
);

create table "Tbl_DBBackup_Case" (
    "Field_Text" varchar2(200),
    "Field_Integer" number
);