package com.github.tesis.runnerms.exception

data class ClientException(
    val code: String = CLIENT_EXCEPTION,
    val messages: List<String> = listOf()
) : RuntimeException()
