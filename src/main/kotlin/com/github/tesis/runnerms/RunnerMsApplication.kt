package com.github.tesis.runnerms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class RunnerMsApplication

fun main(args: Array<String>) {
    runApplication<RunnerMsApplication>(*args)
}
