insert into tbl_dbbackup (
field_null,
field_text,
field_integer,
field_numeric,
field_real,
field_blob
) values (
  null,
  'TEXT',
  123,
  123.456,
  789.012,
  'BLOB'
);

insert into "Tbl_DBBackup_Case" ("Field_Text", "Field_Integer") values ('TEXT', 123);