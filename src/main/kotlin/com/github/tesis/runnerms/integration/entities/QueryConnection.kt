package com.github.tesis.runnerms.integration.entities

import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

data class QueryConnection(
    val statement: Statement,
    val resultSet: ResultSet,
    val connection: Connection?
)
