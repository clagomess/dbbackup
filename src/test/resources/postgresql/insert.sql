insert into tbl_dbbackup (
field_null,
field_float8,
field_numeric,
field_int4,
field_int8,
field_int2,
field_timestamptz,
field_timestamp,
field_date,
field_text,
field_varchar,
field_bytea,
field_time,
field_boolean
) values (
null,
123.45,
678,
910,
123,
456,
now(),
now(),
now(),
'TEXT',
'VARCHAR',
'BLOB',
now(),
true
);

insert into "Tbl_DBBackup_Case" ("Field_Text", "Field_Integer") values ('TEXT', 123);