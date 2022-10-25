package com.github.tesis.runnerms.clients

import com.github.tesis.runnerms.clients.configurations.FeignConfiguration
import com.github.tesis.runnerms.constants.ExternalRoutes
import com.github.tesis.runnerms.constants.HEADER_USER
import com.github.tesis.runnerms.domain.requests.QueryArraySearchRequest
import com.github.tesis.runnerms.domain.responses.QueryArraySearchResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import java.util.UUID

@FeignClient(
    name = ExternalRoutes.Query.NAME,
    url = "\${microservices.query}", configuration = [FeignConfiguration::class]
)
interface QueryClient {
    @PostMapping(
        value = [ExternalRoutes.Query.GET_QUERIES],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getQueriesFromArray(
        @RequestBody queryArraySearchRequest: QueryArraySearchRequest,
        @RequestHeader(HEADER_USER) userCode: UUID
    ): List<QueryArraySearchResponse>
}
