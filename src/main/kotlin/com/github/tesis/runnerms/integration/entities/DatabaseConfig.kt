package com.github.tesis.runnerms.integration.entities

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.zaxxer.hikari.HikariConfig

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DatabaseConfig(
    var host: String,
    var port: Int,
    var name: String,
    var username: String,
    var password: String,
    var ssl: Boolean = false,
    var timeoutConnection: Long = 20000,
    var maxPoolSize: Int = 5
) {
    fun hikariConfig(): HikariConfig =
        HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://$host:$port/$name"
            username = this@DatabaseConfig.username
            password = this@DatabaseConfig.password
            connectionTimeout = timeoutConnection
            maximumPoolSize = maxPoolSize
            driverClassName = "org.postgresql.Driver"
            leakDetectionThreshold = 60000
            maxLifetime = 90000
            idleTimeout = 60000
            minimumIdle = 0
        }
}
