select
c.table_name,
c.column_name,
c.udt_name as data_type,
CASE WHEN is_nullable = 'YES' THEN true ELSE false END "nullable",
coalesce(character_maximum_length, numeric_precision) "precision",
numeric_scale "scale"
from information_schema.columns c
JOIN information_schema.tables t
  on t.table_catalog = c.table_catalog
  AND t.table_schema = c.table_schema
  AND t.table_name = c.table_name
where c.table_schema = :TABLE_SCHEMA AND t.table_type <> 'VIEW'
AND (:TABLE_NAME IS NULL OR c.table_name in (:TABLE_NAME_AR))
order by c.table_name, c.ordinal_position
