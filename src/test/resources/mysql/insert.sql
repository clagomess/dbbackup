insert into tbl_dbbackup (
field_null,
field_int,
field_bigint,
field_decimal,
field_tinyint,
field_datetime,
field_date,
field_blob,
field_longblob,
field_longtext,
field_varchar,
field_text,
field_time,
`Field_Case`
) values (
null,
123,
456,
789.012,
3,
now(),
now(),
'BLOB',
'LONGBLOB',
'LONGTEXT',
'VARCHAR',
'TEXT',
now(),
'Field_Case'
);

insert into `Tbl_DBBackup_Case` (`Field_Text`, `Field_Integer`) values ('TEXT', 123);