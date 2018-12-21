insert into tbl_dbbackup (
field_null,
field_number,
field_number_precision,
field_date,
field_blob,
field_clob,
field_varchar2
) values (
null,
123,
456.78,
SYSDATE,
utl_raw.cast_to_raw('BLOB'),
'CLOB',
'VARCHAR2'
);