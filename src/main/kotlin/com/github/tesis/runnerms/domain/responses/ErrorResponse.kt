package com.github.tesis.runnerms.domain.responses

data class ErrorResponse(
    val code: String,
    val messages: List<String>
)
