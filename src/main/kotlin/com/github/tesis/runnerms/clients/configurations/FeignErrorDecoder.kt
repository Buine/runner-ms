package com.github.tesis.runnerms.clients.configurations

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tesis.runnerms.domain.responses.ErrorResponse
import com.github.tesis.runnerms.exception.CLIENT_ERROR_MESSAGE
import com.github.tesis.runnerms.exception.ClientException
import com.github.tesis.runnerms.utils.LoggerDelegate
import feign.Response
import feign.codec.ErrorDecoder

class FeignErrorDecoder : ErrorDecoder {

    private val logger by LoggerDelegate()

    override fun decode(methodKey: String?, response: Response): RuntimeException {

        logger.error(
            "FEIGN_ERROR_DECODER " +
                "--METHOD_KEY: $methodKey " +
                "--STATUS ${response.status()}"
        )

        return try {
            val currentResponse = response.body().asInputStream().readAllBytes().decodeToString()
            val errorResponse = jacksonObjectMapper().readValue(currentResponse, object : TypeReference<ErrorResponse>() {})
            ClientException(code = errorResponse.code, messages = errorResponse.messages)
        } catch (ex: Exception) {
            ex.printStackTrace()
            ClientException(messages = listOf(String.format(CLIENT_ERROR_MESSAGE, methodKey, response.status())))
        }
    }
}
