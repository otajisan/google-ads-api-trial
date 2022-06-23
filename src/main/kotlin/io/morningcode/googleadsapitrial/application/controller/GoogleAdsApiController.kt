package io.morningcode.googleadsapitrial.application.controller

import io.morningcode.googleadsapitrial.application.output.GetAccessibleCustomersOutputData
import io.morningcode.googleadsapitrial.domain.service.AccountHierarchyService
import io.morningcode.googleadsapitrial.domain.service.CampaignService
import io.morningcode.googleadsapitrial.domain.service.CustomerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ads")
class GoogleAdsApiController(
    private val customers: CustomerService,
    private val accountHierarchyService: AccountHierarchyService,
    private val campaigns: CampaignService
) {

  /**
   * Get accessible customers accounts
   */
  @Operation(summary = "get accessible customers accounts")
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
  fun getAccessibleCustomers(): GetAccessibleCustomersOutputData = customers.asList()

  /**
   * Get account hierarchy
   */
  @Operation(summary = "get account hierarchy")
  @ApiResponses(
      value = [
        ApiResponse(responseCode = "200", description = "Success"),
        ApiResponse(responseCode = "400", description = "Bad request"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "500", description = "Internal server error")
      ]
  )
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = ["/account/hierarchy/{customerId}"])
  fun getAccountHierarchy(@PathVariable("customerId") customerId: Long) {
    accountHierarchyService.asList(customerId)
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
  fun getCampaigns(@PathVariable("customerId") customerId: Long) {
    campaigns.asList(customerId)
  }

  /**
   * Get campaign list by Login Customer ID
   *
   * @param loginCustomerId Google Ads Login Customer ID
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
  @GetMapping(value = ["/campaigns/{loginCustomerId}/{customerId}"])
  fun getCampaignsByLoginCustomerId(
      @PathVariable("loginCustomerId") loginCustomerId: Long,
      @PathVariable("customerId") customerId: Long
  ) {
    campaigns.asList(loginCustomerId, customerId)
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