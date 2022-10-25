package com.github.tesis.runnerms.services

import com.github.tesis.runnerms.clients.QueryBuilderClient
import com.github.tesis.runnerms.clients.QueryClient
import com.github.tesis.runnerms.domain.requests.QueryArraySearchRequest
import com.github.tesis.runnerms.domain.requests.QueryBuilderRequest
import com.github.tesis.runnerms.domain.requests.QueryRequest
import com.github.tesis.runnerms.domain.requests.SchemaRequest
import com.github.tesis.runnerms.domain.responses.QueryResponse
import com.github.tesis.runnerms.exception.ClientException
import com.github.tesis.runnerms.integration.PostgresClient
import com.github.tesis.runnerms.integration.entities.DatabaseConfig
import com.github.tesis.runnerms.mapper.QueryMapper
import com.github.tesis.runnerms.repositories.IntegrationRepository
import com.github.tesis.runnerms.utils.LoggerDelegate
import com.github.tesis.runnerms.validators.QueryBuilderValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RunnerService {
    val connections = mutableMapOf<String, PostgresClient>()
    private val logger by LoggerDelegate()

    @Autowired
    lateinit var integrationRepository: IntegrationRepository

    @Autowired
    lateinit var queryBuilderClient: QueryBuilderClient

    @Autowired
    lateinit var queryClient: QueryClient

    fun runQuery(queryRequest: QueryRequest, userCode: UUID): QueryResponse {
        val connectionFind = connections[queryRequest.integrationCode.toString()]
        connectionFind?.let { dbManager ->
            return runQueryWithManager(queryRequest, dbManager)
        }

        val config = queryRequest.config ?: getConfigDatabase(queryRequest.integrationCode, userCode)

        val timeInit = System.nanoTime()
        val dbManager = PostgresClient(config)
        val timeFinish = System.nanoTime()
        logger.info("Connection Time: ${(timeFinish - timeInit).toFloat() / 1000000.toFloat()} ms")
        val queryResponse = runQueryWithManager(queryRequest, dbManager)
        connections[queryRequest.integrationCode.toString()] = dbManager

        return queryResponse
    }

    fun getConfigDatabase(code: UUID, userCode: UUID): DatabaseConfig {
        val integration = integrationRepository.getByCodeAndUserCode(code, userCode).orElseThrow {
            ClientException("DATABASE_DATA_NOT_FOUND", listOf("Integration not exists"))
        }

        return integration.toConfig()
    }

    fun schemaDatabase(schemaRequest: SchemaRequest, userCode: UUID): Any {
        val connectionFind = connections[schemaRequest.integrationCode.toString()]
        connectionFind?.let { dbManager ->
            return dbManager.generateSchema()
        }
        val config = schemaRequest.config ?: getConfigDatabase(schemaRequest.integrationCode, userCode)
        val timeInit = System.nanoTime()
        val dbManager = PostgresClient(config)
        val timeFinish = System.nanoTime()
        logger.info("Connection Time: ${(timeFinish - timeInit).toFloat() / 1000000.toFloat()} ms")

        return dbManager.generateSchema()
    }

    fun queryBuilderRunner(request: QueryBuilderRequest, userCode: UUID): QueryResponse {
        val queriesCode = QueryBuilderValidator.validateQueryBuilderAndGetQueriesCode(request)
        val configIntegration = getConfigDatabase(request.integrationCode, userCode)
        request.queriesFromIntegrationCode = getQueriesByCode(request.integrationCode, queriesCode, userCode)
        val queryRequest = queryBuilderClient.translateToSql(request, userCode).apply {
            config = configIntegration
        }
        return runQuery(queryRequest, userCode)
    }

    private fun runQueryWithManager(queryRequest: QueryRequest, dbManager: PostgresClient): QueryResponse {
        val timeInit = System.nanoTime()
        val query = QueryMapper.queryToResponse(dbManager.executeQuery(queryRequest.sqlQuery, connections, queryRequest.integrationCode.toString()))
        val count = queryRequest.countQuery?.let {
            QueryMapper.queryToResponse(
                dbManager.executeQuery(
                    it,
                    connections,
                    queryRequest.integrationCode.toString()
                )
            )
        }
        val timeFinish = System.nanoTime()
        logger.info("Query Time: ${(timeFinish - timeInit).toFloat() / 1000000.toFloat()} ms")
        return QueryResponse(data = query.data, schema = query.schema, count = count?.data, sql = queryRequest.sqlQuery)
    }

    private fun getQueriesByCode(integrationCode: UUID, queriesCode: List<UUID>, userCode: UUID): Map<UUID, String> {
        val queriesMapped = mutableMapOf<UUID, String>()
        if (queriesCode.isNotEmpty()) {
            val queries = queryClient.getQueriesFromArray(
                QueryArraySearchRequest(
                    integrationCode = integrationCode,
                    queries = queriesCode.distinct()
                ),
                userCode
            )

            queries.forEach { query ->
                queriesMapped[query.code] = query.sql
            }
        }

        return queriesMapped
    }
}
