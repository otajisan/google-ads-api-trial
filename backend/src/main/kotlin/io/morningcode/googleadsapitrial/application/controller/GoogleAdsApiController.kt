package io.morningcode.googleadsapitrial.application.controller

import io.morningcode.googleadsapitrial.application.input.CreateNewAdGroupRequest
import io.morningcode.googleadsapitrial.application.input.CreateNewCampaignRequest
import io.morningcode.googleadsapitrial.application.input.CreateNewPlacementsRequest
import io.morningcode.googleadsapitrial.application.output.GetAccessibleCustomersOutputData
import io.morningcode.googleadsapitrial.domain.service.*
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
    private val campaignService: CampaignService,
    private val adGroupService: AdGroupService,
    private val searchMetricsService: SearchMetricsService
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
    @GetMapping(value = ["/account/hierarchy/{customerId}/{managerId}"])
    fun getAccountHierarchyByManagerId(
        @PathVariable("customerId") customerId: Long,
        @PathVariable("managerId") managerId: Long
    ) {
        accountHierarchyService.asList(customerId, managerId)
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
        campaignService.asList(customerId)
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
        campaignService.asList(loginCustomerId, customerId)
    }

    /**
     * Get ad group list by Login Customer ID
     *
     * @param customerId Google Ads Customer ID
     * @return Response message "pong"
     */
    @Operation(summary = "get customer ad groups")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad request"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = ["/adgroups/{loginCustomerId}/{customerId}"])
    fun getAdGroups(
        @PathVariable("loginCustomerId") loginCustomerId: Long,
        @PathVariable("customerId") customerId: Long
    ) {
        adGroupService.asList(loginCustomerId, customerId)
    }

    /**
     * Create new campaign
     *
     * @param loginCustomerId Google Ads Login Customer ID
     * @param customerId Google Ads Customer ID
     * @return Response message "pong"
     */
    @Operation(summary = "create new campaign")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad request"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = ["/campaigns/{loginCustomerId}/{customerId}"])
    fun createNewCampaign(
        @PathVariable("loginCustomerId") loginCustomerId: Long,
        @PathVariable("customerId") customerId: Long,
        @RequestBody request: CreateNewCampaignRequest
    ) {
        campaignService.save(loginCustomerId, customerId, request.videoId)
    }

    /**
     * Create new Ad Group
     *
     * @param loginCustomerId Google Ads Login Customer ID
     * @param customerId Google Ads Customer ID
     * @return Response message "pong"
     */
    @Operation(summary = "create new ad group")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad request"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = ["/adgroup/{loginCustomerId}/{customerId}"])
    fun createNewAdGroup(
        @PathVariable("loginCustomerId") loginCustomerId: Long,
        @PathVariable("customerId") customerId: Long,
        @RequestBody request: CreateNewAdGroupRequest
    ) {
        adGroupService.save(loginCustomerId, customerId, request.campaignId)
    }

    /**
     * Add video placements
     *
     * @param loginCustomerId Google Ads Login Customer ID
     * @param customerId Google Ads Customer ID
     * @return Response message "pong"
     */
    @Operation(summary = "create new ad group")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Success"),
            ApiResponse(responseCode = "400", description = "Bad request"),
            ApiResponse(responseCode = "401", description = "Unauthorized"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = ["/adgroup/{loginCustomerId}/{customerId}/placements"])
    fun adPlacementsToAdGroup(
        @PathVariable("loginCustomerId") loginCustomerId: Long,
        @PathVariable("customerId") customerId: Long,
        @RequestBody request: CreateNewPlacementsRequest
    ) {
        adGroupService.addPlacements(
            loginCustomerId,
            customerId,
            request.campaignId,
            request.adGroupId,
            request.videoIds
        )
    }

    /**
     * Get metrics by Campaign ID
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
    @GetMapping(value = ["/metrics/{loginCustomerId}/{customerId}/{campaignId}"])
    fun getMetricsByCampaign(
        @PathVariable("loginCustomerId") loginCustomerId: Long,
        @PathVariable("customerId") customerId: Long,
        @PathVariable("campaignId") campaignId: Long
    ) {
        searchMetricsService.byCampaignId(loginCustomerId, customerId, campaignId)
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
