package io.morningcode.googleauthgateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GoogleAuthGatewayApplication

fun main(args: Array<String>) {
	runApplication<GoogleAuthGatewayApplication>(*args)
}
