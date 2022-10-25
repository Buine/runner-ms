package com.github.tesis.runnerms.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.github.tesis.runnerms.domain.responses.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ClientException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(e: ClientException): ErrorResponse =
        ErrorResponse(
            code = e.code,
            messages = e.messages
        )

    @ExceptionHandler(InvalidFormatException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidFormatException(e: InvalidFormatException): ErrorResponse =
        ErrorResponse(
            code = HttpStatus.BAD_REQUEST.value().toString(),
            messages = listOf(e.originalMessage)
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ErrorResponse =
        when (val cause = e.cause) {
            is InvalidFormatException -> {
                ErrorResponse(
                    code = HttpStatus.BAD_REQUEST.value().toString(),
                    messages = listOf(cause.originalMessage)
                )
            }
            is MissingKotlinParameterException -> {
                ErrorResponse(
                    code = HttpStatus.BAD_REQUEST.value().toString(),
                    messages = listOf(cause.originalMessage)
                )
            }
            else -> {
                ErrorResponse(
                    code = HttpStatus.BAD_REQUEST.value().toString(),
                    messages = listOf(e.message ?: e.localizedMessage)
                )
            }
        }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ErrorResponse =
        ErrorResponse(
            code = HttpStatus.BAD_REQUEST.value().toString(),
            messages = e.bindingResult.fieldErrors.map {
                "${it.field} ${it.defaultMessage}"
            }
        )
}
