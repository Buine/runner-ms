package com.github.tesis.runnerms.integration

import com.github.tesis.runnerms.constants.ErrorCodes
import com.github.tesis.runnerms.exception.ClientException
import com.github.tesis.runnerms.integration.entities.DatabaseConfig
import com.github.tesis.runnerms.integration.entities.QueryConnection
import com.github.tesis.runnerms.mapper.SchemaMapper
import com.github.tesis.runnerms.utils.getQueryFromFile
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class PostgresClient(private val databaseConfig: DatabaseConfig) {
    private var client: HikariDataSource = try {
        HikariDataSource(databaseConfig.hikariConfig())
    } catch (err: Exception) {
        throw ClientException(ErrorCodes.DATABASE_CONNECTION_FAILED)
    }

    private fun getConnection(
        connections: MutableMap<String, PostgresClient>? = null,
        integrationId: String = ""
    ): Connection {
        try {
            client = if (client.isClosed) HikariDataSource(databaseConfig.hikariConfig()) else client
            return client.connection
        } catch (err: Exception) {
            connections?.let {
                it.remove(integrationId)
            }
            throw ClientException(ErrorCodes.DATABASE_CONNECTION_FAILED)
        }
    }

    private fun close() = client.close()

    fun executeQuery(
        query: String,
        connections: MutableMap<String, PostgresClient>?,
        integrationId: String
    ): QueryConnection {
        val initTime = System.nanoTime()
        val connection = getConnection(connections, integrationId)
        val ps: PreparedStatement
        val timeoutStatement = (databaseConfig.hikariConfig().leakDetectionThreshold / 1000).toInt()
        try {
            ps = connection.prepareStatement(query)
            ps.queryTimeout = timeoutStatement
            val result = ps.executeQuery()
            val finishTime = System.nanoTime()
            println("Query time executed in ${(finishTime - initTime).toFloat() / 1000000.toFloat()} ms, $query")
            return QueryConnection(ps, result, connection)
        } catch (err: Exception) {
            close()
            throw ClientException(ErrorCodes.EXECUTE_QUERY_FAILED)
        }
    }

    fun generateSchema(): Map<String, Any> = getTablesWithSchemas()

    private fun getTablesPrimaryKeys(connection: Connection): Map<Pair<String, String>, MutableList<String>> {
        var tablesPrimaryKeys: Map<Pair<String, String>, MutableList<String>>
        connection.let {
            val primaryKeysQuery = getQueryFromFile("queries/primaryKeysPostgres.sql")
            val query = executeQuery(primaryKeysQuery, null, "")
            val rs: ResultSet = query.resultSet
            tablesPrimaryKeys = SchemaMapper.getMapPrimaryKeys(rs)
            query.statement.close()
            query.connection?.close()
        }
        return tablesPrimaryKeys
    }

    private fun getTablesWithSchemas(): Map<String, Any> {
        var schemaTables: MutableMap<String, Any>
        val connection = getConnection()
        connection.let { currentConnection ->
            val tablesPrimaryKey = getTablesPrimaryKeys(currentConnection)
            val tablesForeignKey = getForeignKeys(currentConnection)
            println(tablesForeignKey)
            val columnsQuery = getQueryFromFile("queries/columnsPostgres.sql")
            val query = executeQuery(columnsQuery, null, "")
            val rs: ResultSet = query.resultSet
            val initTime = System.nanoTime()
            schemaTables = SchemaMapper.getMapSchemaTables(rs, tablesPrimaryKey, tablesForeignKey)
            val finishTime = System.nanoTime()
            println("Mapper time executed in ${(finishTime - initTime).toFloat() / 1000000.toFloat()} ms")
            val databaseName = getNameDatabase(currentConnection)
            schemaTables["name"] = databaseName
            query.statement.close()
            query.connection?.close()
            client.close()
        }

        return schemaTables
    }

    private fun getForeignKeys(connection: Connection): Map<Pair<String, String>, MutableList<Map<String, String>>> {
        val foreignKeys: Map<Pair<String, String>, MutableList<Map<String, String>>>
        connection.let {
            val foreignKeysQuery = getQueryFromFile("queries/foreignKeysPostgres.sql")
            val query = executeQuery(foreignKeysQuery, null, "")
            val rs: ResultSet = query.resultSet
            foreignKeys = SchemaMapper.getMapForeignKeys(rs)
            query.statement.close()
            query.connection?.close()
        }
        return foreignKeys
    }

    private fun getNameDatabase(connection: Connection?): String {
        return connection?.catalog ?: ""
    }
}
