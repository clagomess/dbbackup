insert into tbl_dbbackup_pk values (1, 2);

insert into tbl_dbbackup (
field_null,
field_bigint,
field_numeric,
field_decimal,
field_integer,
field_float,
field_real,
field_double,
field_smallint,
field_tinyint,
field_timestamp,
field_time,
field_date,
field_blob,
field_clob,
field_varchar,
field_varchar_ignorecase,
field_boolean
) values (
null,
123,
456,
789.01,
234,
567.89,
123.45,
678.91,
234,
56,
now(),
now(),
now(),
'424c4f42',
'CLOB',
'VARCHAR',
'VARCHAR_IGNORECASE',
true
);

insert into "Tbl_DBBackup_Case" ("Field_Text", "Field_Integer") values ('TEXT', 123);