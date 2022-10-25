package com.github.tesis.runnerms.controllers

import com.github.tesis.runnerms.constants.HEADER_USER
import com.github.tesis.runnerms.constants.Routes
import com.github.tesis.runnerms.domain.requests.QueryBuilderRequest
import com.github.tesis.runnerms.domain.requests.QueryRequest
import com.github.tesis.runnerms.domain.requests.SchemaRequest
import com.github.tesis.runnerms.domain.responses.QueryResponse
import com.github.tesis.runnerms.services.RunnerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("${Routes.Runner.V1}${Routes.Runner.RUNNER_ROUTE}")
class RunnerController {

    @Autowired
    lateinit var runnerService: RunnerService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun runner(
        @RequestBody queryRequest: QueryRequest,
        @RequestHeader(HEADER_USER) userCode: UUID
    ): ResponseEntity<QueryResponse> {
        return ResponseEntity(runnerService.runQuery(queryRequest, userCode), HttpStatus.OK)
    }

    @PostMapping(
        value = [Routes.Runner.SCHEMA_ROUTE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun schema(
        @RequestBody schemaRequest: SchemaRequest,
        @RequestHeader(HEADER_USER) userCode: UUID
    ): ResponseEntity<Any> {
        return ResponseEntity(runnerService.schemaDatabase(schemaRequest, userCode), HttpStatus.OK)
    }

    @PostMapping(
        value = [Routes.Runner.QUERY_ROUTE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun query(
        @RequestBody queryBuilderRequest: QueryBuilderRequest,
        @RequestHeader(HEADER_USER) userCode: UUID
    ): ResponseEntity<QueryResponse> {
        return ResponseEntity(runnerService.queryBuilderRunner(queryBuilderRequest, userCode), HttpStatus.OK)
    }
}
