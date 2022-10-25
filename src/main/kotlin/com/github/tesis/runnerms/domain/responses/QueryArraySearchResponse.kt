package com.github.tesis.runnerms.domain.responses

import java.util.UUID

data class QueryArraySearchResponse(
    val code: UUID,
    val name: String,
    val sql: String
)
