package com.github.tesis.runnerms.mapper

import java.sql.ResultSet

object SchemaMapper {
    fun getMapPrimaryKeys(rs: ResultSet): Map<Pair<String, String>, MutableList<String>> {
        val tablesPrimaryKeys = mutableMapOf<Pair<String, String>, MutableList<String>>()
        while (rs.next()) {
            val schemaTable = rs.getString("table_schema")
            val tableName = rs.getString("table_name")
            val primaryKey = rs.getString("primary_key")

            val pair = Pair<String, String>(schemaTable, tableName)

            if (!tablesPrimaryKeys.containsKey(pair)) {
                tablesPrimaryKeys[pair] = mutableListOf()
            }
            tablesPrimaryKeys[pair]!!.add(primaryKey)
        }
        return tablesPrimaryKeys
    }

    fun getMapForeignKeys(rs: ResultSet): Map<Pair<String, String>, MutableList<Map<String, String>>> {
        val tablesForeignKeys = mutableMapOf<Pair<String, String>, MutableList<Map<String, String>>>()
        while (rs.next()) {
            val tableSchema = rs.getString("table_schema")
            val tableName = rs.getString("table_name")
            val columnName = rs.getString("column_name")
            val foreignTableSchema = rs.getString("foreign_table_schema")
            val foreignTableName = rs.getString("foreign_table_name")
            val foreignColumnName = rs.getString("foreign_column_name")

            val pairTable = Pair<String, String>(tableSchema, tableName)
            val pairForeignTable = Pair<String, String>(foreignTableSchema, foreignTableName)

            if (!tablesForeignKeys.containsKey(pairTable)) {
                tablesForeignKeys[pairTable] = mutableListOf()
            }
            if (!tablesForeignKeys.containsKey(pairForeignTable)) {
                tablesForeignKeys[pairForeignTable] = mutableListOf()
            }
            val mapForeign = mapOf<String, String>(
                "table_schema" to tableSchema,
                "table_name" to tableName,
                "column_name" to columnName,
                "foreign_table_schema" to foreignTableSchema,
                "foreign_table_name" to foreignTableName,
                "foreign_column_name" to foreignColumnName,
            )
            tablesForeignKeys[pairTable]!!.add(mapForeign)
            tablesForeignKeys[pairForeignTable]!!.add(mapForeign)
        }
        return tablesForeignKeys
    }

    fun getMapSchemaTables(rs: ResultSet, mapPrimaryKeys: Map<Pair<String, String>, MutableList<String>>, mapForeignKey: Map<Pair<String, String>, MutableList<Map<String, String>>>): MutableMap<String, Any> {
        val schemas = mutableListOf<Map<String, Any>>()
        var tables = mutableMapOf<String, Any>()
        var currentSchema = ""
        while (rs.next()) {
            val schemaName = rs.getString("table_schema")
            val tableName = rs.getString("table_name")
            val columnName = rs.getString("column_name")
            val dataType = rs.getString("data_type")
            val isNullable = rs.getString("is_nullable").equals("YES")
            val isAuto = rs.getString("column_default")?.contains("nextval") ?: false
            if (currentSchema == "") {
                currentSchema = schemaName
            }

            if (currentSchema != schemaName) {
                schemas.add(
                    mapOf(
                        "name" to currentSchema,
                        "tables" to tables.values.toList()
                    )
                )
                currentSchema = schemaName
                tables = mutableMapOf()
            }

            if (!tables.containsKey(tableName)) {
                tables[tableName] = mutableMapOf(
                    "name" to tableName,
                    "primary_key" to mapPrimaryKeys[Pair<String, String>(schemaName, tableName)],
                    "foreign_key" to mapForeignKey[Pair<String, String>(schemaName, tableName)],
                    "columns" to mutableListOf<Map<String, Any>>()
                )
            }

            val columns = getListColumns(tableName, tables)
            columns.add(
                mapOf(
                    "name" to columnName,
                    "data_type" to dataType,
                    "is_auto" to isAuto,
                    "constraints" to mapOf("is_nullable" to isNullable)
                )
            )
        }
        schemas.add(
            mapOf(
                "name" to currentSchema,
                "tables" to tables.values.toList()
            )
        )
        return mutableMapOf(
            "name" to "",
            "schemas" to schemas
        )
    }

    private fun getListColumns(tableName: String, tables: Map<String, Any>): MutableList<Map<String, Any>> {
        val columns = (tables[tableName] as Map<String, Any>)["columns"]
        return columns as MutableList<Map<String, Any>>
    }
}
