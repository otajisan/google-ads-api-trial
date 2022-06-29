package io.morningcode.googleauthgateway.application.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController {

  @GetMapping("/ping")
  fun ping(): String = "pong"
}