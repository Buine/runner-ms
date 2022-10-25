package com.github.tesis.runnerms.mapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tesis.runnerms.integration.entities.QueryConnection
import java.sql.Types

data class QueryMapperResponse(
    val data: Any,
    val schema: Any
)

object QueryMapper {
    fun queryToResponse(queryResponse: QueryConnection): QueryMapperResponse {
        val results = mutableListOf<MutableMap<String, Any?>>()
        val columns = mutableListOf<String>()
        val schema = mutableListOf<Map<String, String>>()
        for (i in 1..queryResponse.resultSet.metaData.columnCount) {
            columns.add(queryResponse.resultSet.metaData.getColumnName(i))
            schema.add(
                mapOf(
                    "name" to queryResponse.resultSet.metaData.getColumnName(i),
                    "data_type" to queryResponse.resultSet.metaData.getColumnTypeName(i),
                )
            )
        }

        while (queryResponse.resultSet.next()) {
            val row = mutableMapOf<String, Any?>()
            columns.forEachIndexed { idx, column ->
                row[column] = if (queryResponse.resultSet.metaData.getColumnType(idx + 1) == Types.JAVA_OBJECT) {
                    jacksonObjectMapper().writeValueAsString(queryResponse.resultSet.getObject(column))
                } else {
                    queryResponse.resultSet.getObject(column)
                }
            }
            results.add(row)
        }
        queryResponse.resultSet.close()
        queryResponse.statement.close()
        queryResponse.connection?.close()
        return QueryMapperResponse(
            data = results,
            schema = schema
        )
    }
}
