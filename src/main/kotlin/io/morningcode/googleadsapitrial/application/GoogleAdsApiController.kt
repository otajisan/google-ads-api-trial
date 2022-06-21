package io.morningcode.googleadsapitrial.application

import io.morningcode.googleadsapitrial.domain.Campaigns
import io.morningcode.googleadsapitrial.domain.Customers
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ads")
class GoogleAdsApiController(
    private val customers: Customers,
    private val campaigns: Campaigns
) {

  /**
   * Get accessible customers
   */
  @Operation(summary = "get accessible customers")
  @ApiResponses(
      value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "500", description = "Internal server error")
      ]
  )
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = ["/customers/accessible"])
  fun getAccessibleCustomers() {
    customers.asList()
  }

  /**
   * Get campaign list by Customer ID
   *
   * @param customerId Google Ads Customer ID
   * @return Response message "pong"
   */
  @Operation(summary = "get customer campaigns")
  @ApiResponses(
      value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "500", description = "Internal server error")
      ]
  )
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = ["/campaigns/{customerId}"])
  fun getCampaigns(@PathVariable("customerId") customerId: String) {
    campaigns.asList(customerId)
  }

  /**
   * Greeting Message
   *
   * @param name your name please
   * @return Response message "pong"
   */
  @Operation(summary = "greeting")
  @ApiResponses(
      value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "500", description = "Internal server error")
      ]
  )
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = ["/greeting/{name}"])
  fun greeting(@PathVariable("name") name: String): String = "Hello ${name}!"

  /**
   * Ping
   *
   * @return Response message "pong"
   */
  @Operation(summary = "ping")
  @ApiResponse(responseCode = "200", description = "Success")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = ["/ping"])
  fun ping(): String = "pong"
}