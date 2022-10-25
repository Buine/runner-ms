package com.github.tesis.runnerms.domain.requests

import java.util.UUID

data class QueryArraySearchRequest(
    val integrationCode: UUID,
    val queries: List<UUID>
)
