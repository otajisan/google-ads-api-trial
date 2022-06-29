package io.morningcode.googleadsapitrial.application.controller

import io.morningcode.googleadsapitrial.application.input.AuthRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth/google")
class OAuth2Controller {

//  @Operation(summary = "Get access token")
//  @ApiResponses(value = [
//    ApiResponse(responseCode = "200", description = "OK"),
//    ApiResponse(responseCode = "400", description = "Bad request"),
//    ApiResponse(responseCode = "401", description = "Unauthorized"),
//    ApiResponse(responseCode = "403", description = "Forbidden"),
//    ApiResponse(responseCode = "404", description = "Not found"),
//    ApiResponse(responseCode = "500", description = "Internal server error")
//  ])
//  @GetMapping("")
//  fun getAccessToken(): String {
//    return "access_token"
//  }

  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "OK"),
    ApiResponse(responseCode = "400", description = "Bad request"),
    ApiResponse(responseCode = "401", description = "Unauthorized"),
    ApiResponse(responseCode = "403", description = "Forbidden"),
    ApiResponse(responseCode = "404", description = "Not found"),
    ApiResponse(responseCode = "500", description = "Internal server error")
  ])
  @PostMapping(value = ["/token"], produces = ["application/json;charset=UTF-8"])
  fun getAccessToken(@RequestBody request: String) {
    println(request)
  }

  @GetMapping(value = ["/test/{code}"])
  fun test(@PathVariable code: String): String {
    println(code)
    return code
  }

  @ApiResponses(value = [
    ApiResponse(responseCode = "200", description = "OK"),
    ApiResponse(responseCode = "400", description = "Bad request"),
    ApiResponse(responseCode = "401", description = "Unauthorized"),
    ApiResponse(responseCode = "403", description = "Forbidden"),
    ApiResponse(responseCode = "404", description = "Not found"),
    ApiResponse(responseCode = "500", description = "Internal server error")
  ])
  @GetMapping("/authenticated")
  fun getUser(
      @AuthenticationPrincipal user: OidcUser,
      model: Model
  ): String {
    return "user"
  }
}