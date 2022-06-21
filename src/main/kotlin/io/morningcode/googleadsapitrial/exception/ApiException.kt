package io.morningcode.googleadsapitrial.exception

import org.springframework.http.HttpStatus

abstract class ApiException(val statusCode: HttpStatus, message: String? = null) : RuntimeException(message)