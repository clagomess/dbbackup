select table_name, column_name, data_type
from information_schema.columns where table_schema = :TABLE_SCHEMA
and (:TABLE_NAME IS NULL OR table_name in (:TABLE_NAME_AR))
order by table_name, ordinal_position