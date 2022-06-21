package io.morningcode.googleadsapitrial.exception

import org.springframework.http.HttpStatus

class ApiUnexpectedException(message: String? = null) : ApiException(HttpStatus.INTERNAL_SERVER_ERROR, message)