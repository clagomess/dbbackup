select
sm.name "table_name",
pti.name "column_name",
pti.type "data_type",
CASE WHEN pti."notnull" = 0 THEN 1 ELSE 0 END "nullable",
0 "precision",
0 "scale"
from sqlite_master sm
join pragma_table_info(sm.name) pti
where sm.type = 'table'
AND (:TABLE_NAME IS NULL OR sm.name in (:TABLE_NAME_AR))
order by sm.name, pti.cid
