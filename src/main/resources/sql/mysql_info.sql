select
c.table_name "table_name",
count(*) "qtd_columns",
max(case when c.column_key = 'PRI' then c.column_name else null end) "pk_column",
max(case when c.data_type in ('blob', 'longblob', 'longtext') then 1 else 0 end) "lob"
from information_schema.columns c
where c.table_schema = :TABLE_SCHEMA
group by c.table_name
order by c.table_name