package com.github.tesis.runnerms.domain.requests

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.github.tesis.runnerms.integration.entities.DatabaseConfig
import java.util.UUID

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SchemaRequest(
    var integrationCode: UUID,
    var config: DatabaseConfig?
)
