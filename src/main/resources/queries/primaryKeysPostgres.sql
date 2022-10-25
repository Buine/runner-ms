select tc.table_schema, tc.table_name, kc.column_name as primary_key
from information_schema.table_constraints tc
         join
     information_schema.key_column_usage kc on kc.table_name = tc.table_name
         and kc.table_schema = tc.table_schema
         and kc.constraint_name = tc.constraint_name
where tc.constraint_type = 'PRIMARY KEY';