select
table_name,
column_name,
data_type,
IF(is_nullable = 'YES', true, false) nullable,
coalesce(character_maximum_length, numeric_precision) "precision",
numeric_scale scale
from information_schema.columns where table_schema = :TABLE_SCHEMA
and (:TABLE_NAME IS NULL OR table_name in (:TABLE_NAME_AR))
order by table_name, ordinal_position