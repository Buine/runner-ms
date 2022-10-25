package com.github.tesis.runnerms.clients

import com.github.tesis.runnerms.clients.configurations.FeignConfiguration
import com.github.tesis.runnerms.constants.ExternalRoutes
import com.github.tesis.runnerms.constants.HEADER_USER
import com.github.tesis.runnerms.domain.requests.QueryBuilderRequest
import com.github.tesis.runnerms.domain.requests.QueryRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import java.util.UUID

@FeignClient(
    name = ExternalRoutes.QueryBuilder.NAME,
    url = "\${microservices.querybuilder}", configuration = [FeignConfiguration::class]
)
interface QueryBuilderClient {
    @PostMapping(
        value = [ExternalRoutes.QueryBuilder.TRANSLATE_SQL],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun translateToSql(
        @RequestBody queryBuilderRequest: QueryBuilderRequest,
        @RequestHeader(HEADER_USER) userCode: UUID
    ): QueryRequest
}
