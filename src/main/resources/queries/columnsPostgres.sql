select table_schema, table_name, column_name, data_type, is_nullable, column_default
from information_schema.columns
WHERE table_schema not in ('information_schema', 'pg_catalog')
order by table_schema;