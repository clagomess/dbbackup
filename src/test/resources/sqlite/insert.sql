insert into tbl_dbbackup (
field_null,
field_text,
field_integer,
field_numeric,
field_real,
field_blob,
"Field_Case"
) values (
  null,
  'TEXT',
  123,
  123.456,
  789.012,
  'BLOB',
  'Field_Case'
);

insert into "Tbl_DBBackup_Case" ("Field_Text", "Field_Integer") values ('TEXT', 123);