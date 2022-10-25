package com.github.tesis.runnerms.validators

import com.github.tesis.runnerms.domain.enums.TypeColumn
import com.github.tesis.runnerms.domain.enums.TypeConditions
import com.github.tesis.runnerms.domain.enums.TypeParam
import com.github.tesis.runnerms.domain.requests.QueryBuilderRequest
import com.github.tesis.runnerms.exception.ClientException
import java.util.UUID

object QueryBuilderValidator {
    fun validateQueryBuilderAndGetQueriesCode(request: QueryBuilderRequest): List<UUID> {
        val tables = mutableListOf<String>()
        val tablesQuery = mutableListOf<UUID>()
        getTables(request.queryJson.tables, tables, tablesQuery)
        validateColumns(request.queryJson.columns, tables)
        validateFilters(request.queryJson.filters, tables)
        validateGroupBy(request.queryJson.groupBy, tables)
        validateOrderBy(request.queryJson.order, tables)

        return tablesQuery
    }

    private fun getTables(
        table: QueryBuilderRequest.QueryJson.Tables,
        tables: MutableList<String>,
        tablesQuery: MutableList<UUID>,
        join: QueryBuilderRequest.QueryJson.Tables.Join? = null,
    ) {
        if (table.queryCode != null) {
            tablesQuery.add(table.queryCode)
            tables.add(
                table.alias ?: throw ClientException(
                    "BAD_QUERY",
                    listOf("The table query with code '${table.queryCode}' not assigned alias")
                )
            )
        } else {
            tables.add(getTableName(table = table))
        }
        table.join?.let {
            getTables(it.table, tables, tablesQuery, it)
        }
        join?.let {
            getTables(it.table, tables, tablesQuery, it.join)
        }
    }

    private fun getTableName(
        table: QueryBuilderRequest.QueryJson.Tables? = null,
        column: QueryBuilderRequest.QueryJson.Column.TableColumn? = null
    ): String {
        var tableName = ""
        table?.let {
            if (it.alias != null) {
                tableName += it.alias
            } else {
                tableName = "${it.schemaName}.${it.tableName}"
            }
        }
        column?.let {
            if (it.alias != null) {
                tableName += it.alias
            } else {
                tableName = "${it.schemaName}.${it.tableName}"
            }
        }

        return tableName
    }

    private fun validateColumns(listColumns: List<QueryBuilderRequest.QueryJson.Column>, tables: List<String>) {
        listColumns.forEach { column ->
            when (column.type) {
                TypeColumn.COLUMN -> {
                    column.tableColumn?.let {
                        validateTableExistInImport(it, tables)
                    }
                }
                TypeColumn.GENERATED -> {
                    column.generatedColumn?.let { generateColumn ->
                        generateColumn.params.forEach { param ->
                            if (param.type == TypeParam.COLUMN) {
                                param.tableColumn?.let {
                                    validateTableExistInImport(it, tables)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateFilters(listFilters: List<QueryBuilderRequest.QueryJson.Filter>, tables: List<String>) {
        listFilters.forEach { filter ->
            when (filter.type) {
                TypeConditions.GROUP -> {
                    filter.groupConditions?.forEach { currentGroupFilter ->
                        currentGroupFilter.params.forEach { param ->
                            if (param.type == TypeParam.COLUMN) {
                                param.tableColumn?.let {
                                    validateTableExistInImport(it, tables)
                                }
                            }
                        }
                    }
                }
                else -> {
                    filter.params?.forEach { param ->
                        if (param.type == TypeParam.COLUMN) {
                            param.tableColumn?.let {
                                validateTableExistInImport(it, tables)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateGroupBy(
        listGroupBy: List<QueryBuilderRequest.QueryJson.Column.TableColumn>,
        tables: List<String>
    ) {
        listGroupBy.forEach {
            validateTableExistInImport(it, tables)
        }
    }

    private fun validateOrderBy(listOrderBy: List<QueryBuilderRequest.QueryJson.Order>, tables: List<String>) {
        listOrderBy.forEach {
            validateTableExistInImport(it.toTableColumn(), tables)
        }
    }

    private fun validateTableExistInImport(
        column: QueryBuilderRequest.QueryJson.Column.TableColumn,
        tables: List<String>
    ) {
        val tableName = getTableName(column = column)
        if (!tables.contains(tableName)) {
            throw ClientException(
                "BAD_QUERY",
                listOf("The column '${column.columnName ?: column.alias}' from table $tableName, this table not is in list tables")
            )
        }
    }
}
