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
field_bytea
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
'BLOB'
);