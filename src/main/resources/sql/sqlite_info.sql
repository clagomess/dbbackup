select
sm.name "table_name",
count(*) "qtd_columns",
MAX(CASE WHEN pti.pk = 1 THEN pti.name ELSE null END) "data_type",
MAX(CASE WHEN pti.type = 'blob' THEN 1 ELSE 0 END) "lob"
from sqlite_master sm
join pragma_table_info(sm.name) pti
where sm.type = 'table'
GROUP BY sm.name
ORDER BY sm.name