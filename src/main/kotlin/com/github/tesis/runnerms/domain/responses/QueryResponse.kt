package com.github.tesis.runnerms.domain.responses

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class QueryResponse(
    var data: Any,
    var schema: Any,
    var sql: String,
    var count: Any?
)
