select
  t.table_name "table_name",
  count(*) "qtd_columns",
  max(pk.column_name) "pk_column",
  MAX(CASE WHEN c.udt_name IN ('bytea', 'text') THEN 1 ELSE 0 END) "lob"
from information_schema.columns c
join information_schema.tables t
  on t.table_catalog = c.table_catalog
  and t.table_schema = c.table_schema
  and t.table_name = c.table_name
left join (
  select tc.table_name, ccu.column_name
  from information_schema.table_constraints tc
  join information_schema.constraint_column_usage ccu
    on ccu.table_schema = tc.table_schema
    and ccu.table_name = tc.table_name
    and ccu.constraint_name = tc.constraint_name
  where tc.table_schema = :TABLE_SCHEMA
  and tc.constraint_type = 'PRIMARY KEY'
) pk
  on pk.table_name = t.table_name
  and pk.column_name = c.column_name
where c.table_schema = :TABLE_SCHEMA and t.table_type <> 'VIEW'
group by t.table_name
order by t.table_name