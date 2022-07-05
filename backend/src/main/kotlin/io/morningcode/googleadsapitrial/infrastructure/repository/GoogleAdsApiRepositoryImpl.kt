package io.morningcode.googleadsapitrial.infrastructure.repository

import com.google.ads.googleads.lib.GoogleAdsClient
import com.google.ads.googleads.lib.utils.FieldMasks
import com.google.ads.googleads.v11.common.AdVideoAsset
import com.google.ads.googleads.v11.services.*
import com.google.ads.googleads.v11.common.ManualCpc
import com.google.ads.googleads.v11.common.YoutubeVideoAsset
import com.google.ads.googleads.v11.enums.*
import com.google.ads.googleads.v11.resources.*
import com.google.ads.googleads.v11.utils.ResourceNames
import com.google.api.gax.rpc.ServerStream
import com.google.auth.oauth2.UserCredentials
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.google.type.DateTime
import io.grpc.StatusRuntimeException
import io.morningcode.googleadsapitrial.configuration.GoogleAdsConfiguration
import io.morningcode.googleadsapitrial.domain.model.Customer
import io.morningcode.googleadsapitrial.exception.ApiUnexpectedException
import io.morningcode.googleadsapitrial.util.logger
import org.springframework.stereotype.Repository
import java.io.FileNotFoundException
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Stream
import javax.annotation.PostConstruct

@Repository
class GoogleAdsApiRepositoryImpl(
    private val config: GoogleAdsConfiguration
) : GoogleAdsApiRepository {

    companion object {
        private val log by logger()
    }

    private lateinit var googleAdsClient: GoogleAdsClient

    // Example: fetch credentials from ads.properties
    @PostConstruct
    fun init() {
        try {
            googleAdsClient = GoogleAdsClient.newBuilder().fromPropertiesFile().build()
        } catch (ex: FileNotFoundException) {
            log.error("Google Ads configuration file not found.", ex)
            throw ApiUnexpectedException("Google Ads configuration file not found.")
        } catch (ex: IllegalStateException) {
            log.error("Google Ads configuration is invalid.", ex)
            throw ApiUnexpectedException("Google Ads configuration is invalid.")
        } catch (ex: IOException) {
            log.error("Failed to create GoogleAdsClient", ex)
            throw ApiUnexpectedException("Failed to create GoogleAdsClient")
        }
    }

    private fun buildUserCredentials() =
        UserCredentials.newBuilder()
            .setClientId(config.clientId)
            .setClientSecret(config.clientSecret)
            .setRefreshToken(config.refreshToken)
            .build()

    private fun buildClient(loginCustomerId: Long?) =
        buildUserCredentials().let { userCredentials ->
            googleAdsClient.toBuilder()
                .setCredentials(userCredentials)
                .setLoginCustomerId(loginCustomerId)
                .build()
        }

    private fun buildClient() = buildClient(null)

    override fun getAccessibleCustomers(): List<Customer>? {
        val client = buildClient()

        return try {
            client.latestVersion.createCustomerServiceClient().use { customerService ->
                val response =
                    customerService.listAccessibleCustomers(ListAccessibleCustomersRequest.newBuilder().build())
                response.resourceNamesList.map { customerResourceName ->
                    log.info("accessible customer name: $customerResourceName")
                    Customer(customerId = Customer.CustomerId(customerResourceName))
                }
            }
        } catch (ex: IOException) {
            log.error("failed to fetch accessible customers. ", ex)
            null
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to fetch customers.", ex)
            null
        }
    }

    override fun getAccountHierarchy(loginCustomerId: Long, managerId: Long) {
        val client = buildClient(loginCustomerId)

        try {
            val resultHierarchy: Multimap<Long, CustomerClient> = ArrayListMultimap.create()
            client.latestVersion.createGoogleAdsServiceClient().use { googleAdsServiceClient ->
                val query = ("SELECT customer_client.client_customer, customer_client.level, "
                        + "customer_client.manager, customer_client.descriptive_name, "
                        + "customer_client.currency_code, customer_client.time_zone, "
                        + "customer_client.id "
                        + "FROM customer_client "
                        + "WHERE customer_client.level <= 1")

                val searchCustomerIds = LinkedList<Long>()
                searchCustomerIds.add(managerId)

                // search all hierarchy recursively
                while (searchCustomerIds.isNotEmpty()) {
                    val searchCustomerId = searchCustomerIds.poll()
                    // get hierarchies from Google Ads API
                    val response = googleAdsServiceClient.search(
                        SearchGoogleAdsRequest.newBuilder()
                            .setQuery(query)
                            .setCustomerId(searchCustomerId.toString())
                            .build()
                    )
                    response.iterateAll().map { row ->
                        val customerClient = row.customerClient
                        log.info("get the customer. $customerClient")

                        if (customerClient.id != searchCustomerId &&
                            customerClient.id != managerId
                        ) {
                            // put result
                            resultHierarchy.put(searchCustomerId, customerClient)
                            // add next entry
                            if (customerClient.manager && customerClient.level == 1L) {
                                searchCustomerIds.add(customerClient.id)
                            }
                        }
                    }
                }
            }
            log.info("result: $resultHierarchy")
        } catch (ex: IOException) {
            log.error("failed to fetch accessible customers. ", ex)
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to fetch customers.", ex)
        }
    }

    override fun getCampaigns(customerId: Long) = getCampaigns(null, customerId)

    override fun getCampaigns(loginCustomerId: Long?, customerId: Long) {
        val client = buildClient(loginCustomerId)
        try {
            client.latestVersion.createGoogleAdsServiceClient().use { googleAdsServiceClient ->
                val query = "SELECT campaign.id, campaign.name FROM campaign ORDER BY campaign.id"
                val request = SearchGoogleAdsStreamRequest.newBuilder()
                    .setCustomerId(customerId.toString())
                    .setQuery(query)
                    .build()
                val stream = googleAdsServiceClient.searchStreamCallable().call(request)
                stream.map { response ->
                    response.resultsList.forEach { row ->
                        log.info("campaign id: ${row.campaign.id} campaign name: ${row.campaign.name}")
                    }
                }
            }
        } catch (ex: IOException) {
            log.error("failed to fetch campaigns. ", ex)
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to fetch campaigns.", ex)
        }
    }

    override fun getAdGroups(loginCustomerId: Long?, customerId: Long) {
        val client = buildClient(loginCustomerId)
        try {
            client.latestVersion.createGoogleAdsServiceClient().use { googleAdsServiceClient ->
                val query = ("SELECT campaign.id, "
                        + "campaign.name, "
                        + "ad_group.id, "
                        + "ad_group.name, "
                        + "ad_group_criterion.criterion_id, "
                        + "ad_group_criterion.keyword.text, "
                        + "ad_group_criterion.keyword.match_type "
                        //  + "metrics.impressions, "
                        //  + "metrics.clicks, "
                        //  + "metrics.cost_micros, "
                        //  + "metrics.bounce_rate "
                        + "FROM managed_placement_view "
                        //+ "FROM keyword_view "
                        //+ "WHERE segments.date DURING LAST_7_DAYS "
                        //+ "AND campaign.advertising_channel_type = 'VIDEO' "
                        //+ "AND ad_group.status = 'ENABLED' "
                        //+ "ORDER BY metrics.impressions DESC "
                        )
                val request = SearchGoogleAdsStreamRequest.newBuilder()
                    .setCustomerId(customerId.toString())
                    .setQuery(query)
                    .build()
                val stream = googleAdsServiceClient.searchStreamCallable().call(request)
                stream.map { response ->
                    response.resultsList.forEach { row ->
                        log.info("adgroup id: ${row.adGroup.id} adgroup name: ${row.adGroup.name} ${row.adGroupCriterion}")
                    }
                }
            }
        } catch (ex: IOException) {
            log.error("failed to fetch adgroups. ", ex)
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to fetch adgroups.", ex)
        }
    }

    override fun addCampaign(loginCustomerId: Long?, customerId: Long, videoId: String) {
        val client = buildClient(loginCustomerId)

        val budgetResourceName = addCampaignBudget(client, customerId)
        val youtubeVideoAssetResourceName = addYouTubeVideoAsset(client, customerId, videoId)

        val campaign = Campaign.newBuilder()
            .setName("my test campaign name #" + UUID.randomUUID())
            .setAdvertisingChannelType(AdvertisingChannelTypeEnum.AdvertisingChannelType.SEARCH)
            .setStatus(CampaignStatusEnum.CampaignStatus.PAUSED)
            .setManualCpc(ManualCpc.newBuilder().build())
            .setCampaignBudget(budgetResourceName)
            .setStartDate(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .setEndDate(LocalDateTime.now().plusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
            .build()

        val operation = CampaignOperation.newBuilder().setCreate(campaign).build()

        try {
            client.latestVersion.createCampaignServiceClient().use { campaignServiceClient ->
                val response = campaignServiceClient.mutateCampaigns(customerId.toString(), listOf(operation))
                log.info("created new campaign. response: $response")
            }
        } catch (ex: IOException) {
            log.error("failed to fetch campaigns. ", ex)
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to fetch campaigns.", ex)
        }
    }

    private fun addYouTubeVideoAsset(client: GoogleAdsClient, customerId: Long, videoId: String): String {
        val asset = Asset.newBuilder()
            .setName("my youtube video asset #" + UUID.randomUUID())
            .setType(AssetTypeEnum.AssetType.YOUTUBE_VIDEO)
            .setYoutubeVideoAsset(YoutubeVideoAsset.newBuilder().setYoutubeVideoId(videoId))
            .build()
        val operation = AssetOperation.newBuilder().setCreate(asset).build()

        try {
            client.latestVersion.createAssetServiceClient().use { assetServiceClient ->
                val response = assetServiceClient.mutateAssets(customerId.toString(), listOf(operation))
                log.info("created new youtube video asset. response: $response")
                return response.getResults(0).resourceName
            }

        } catch (ex: IOException) {
            log.error("failed to create campaign budget. ", ex)
            throw ApiUnexpectedException("failed to create campaign budget.")
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to create campaign budget.", ex)
            throw ApiUnexpectedException("restricted to create campaign budget.")
        }
    }

    private fun addCampaignBudget(client: GoogleAdsClient, customerId: Long): String {
        val budget = CampaignBudget.newBuilder()
            .setName("my test campaign budget name #" + UUID.randomUUID())
            .setDeliveryMethod(BudgetDeliveryMethodEnum.BudgetDeliveryMethod.STANDARD)
            // NOTE: equals 1 yen
            .setAmountMicros(1000_000)
            .build()

        val operation = CampaignBudgetOperation.newBuilder().setCreate(budget).build()

        try {
            client.latestVersion.createCampaignBudgetServiceClient().use { campaignBudgetServiceClient ->
                val response =
                    campaignBudgetServiceClient.mutateCampaignBudgets(customerId.toString(), listOf(operation))
                log.info("created new campaign budget. response: $response")
                return response.getResults(0).resourceName
            }
        } catch (ex: IOException) {
            log.error("failed to create campaign budget. ", ex)
            throw ApiUnexpectedException("failed to create campaign budget.")
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to create campaign budget.", ex)
            throw ApiUnexpectedException("restricted to create campaign budget.")
        }
    }

    override fun addAdGroup(loginCustomerId: Long?, customerId: Long, campaignId: Long) {
        val client = buildClient(loginCustomerId)
        val campaignResourceName = ResourceNames.campaign(customerId, campaignId)
        val adGroup = AdGroup.newBuilder()
            .setName("my ad group #" + UUID.randomUUID())
            .setStatus(AdGroupStatusEnum.AdGroupStatus.PAUSED)
            .setCampaign(campaignResourceName)
            .setType(AdGroupTypeEnum.AdGroupType.VIDEO_BUMPER)
            .setCpmBidMicros(1000_000L)
            //.setCpcBidMicros(1000_000L)
            .build()

        val operations = mutableListOf<AdGroupOperation>()
        operations.add(AdGroupOperation.newBuilder().setCreate(adGroup).build())

        try {
            client.latestVersion.createAdGroupServiceClient().use { adGroupServiceClient ->
                val response = adGroupServiceClient.mutateAdGroups(customerId.toString(), operations)
                response.resultsList.map { result ->
                    log.info(result.resourceName)
                }
            }
        } catch (ex: IOException) {
            log.error("failed to create ad group. ", ex)
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to create ad group.", ex)
        }
    }

    override fun addVideoPlacements(
        loginCustomerId: Long?,
        customerId: Long,
        campaignId: Long,
        adGroupId: Long,
        videoId: List<String>
    ) {
        val client = buildClient(loginCustomerId)
        val campaignResourceName = ResourceNames.campaign(customerId, campaignId)
        val adGroupResourceName = ResourceNames.adGroup(customerId, adGroupId)

        // TODO
        val criterionId = 1L
        val adGroupCriterionResourceName = ResourceNames.adGroupCriterion(customerId, adGroupId, criterionId)

        val adGroupCriterion = AdGroupCriterion.newBuilder()
            .setResourceName(adGroupCriterionResourceName)
            .build()

        val operation = AdGroupCriterionOperation.newBuilder()
            .setUpdate(adGroupCriterion)
            .setUpdateMask(FieldMasks.allSetFieldsOf(adGroupCriterion))
            .build()


        try {
            client.latestVersion.createAdGroupCriterionServiceClient().use { adGroupServiceClient ->
                val response = adGroupServiceClient.mutateAdGroupCriteria(customerId.toString(), listOf(operation))
                response.resultsList.map { mutateAdGroupCriterionResult ->
                    log.info(mutateAdGroupCriterionResult.resourceName)
                }
            }
        } catch (ex: IOException) {
            log.error("failed to add placements. ", ex)
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to add placements.", ex)
        }

    }

    override fun searchCampaignReport(loginCustomerId: Long?, customerId: Long, campaignId: Long) {
        val client = buildClient(loginCustomerId)
        try {
            client.latestVersion.createGoogleAdsServiceClient().use { googleAdsServiceClient ->
                val query = ("SELECT " +
                        "video.id, " +
                        "video.channel_id, " +
                        "video.resource_name, " +
                        "video.title, " +
                        "video.duration_millis, " +
                        "ad_group.campaign, " +
                        "ad_group.name, " +
                        "ad_group.effective_cpc_bid_micros, " +
                        "ad_group.cpc_bid_micros, " +
                        "ad_group.cpm_bid_micros, " +
                        "ad_group.cpv_bid_micros, " +
                        "ad_group.id, " +
                        "campaign.status, " +
                        "campaign.start_date, " +
                        "campaign.target_cpm, " +
                        "campaign.target_cpa.target_cpa_micros, " +
                        "campaign.target_cpa.cpc_bid_floor_micros, " +
                        "campaign.target_cpa.cpc_bid_ceiling_micros, " +
                        "campaign.target_impression_share.cpc_bid_ceiling_micros, " +
                        "campaign.target_impression_share.location, " +
                        "campaign.target_impression_share.location_fraction_micros, " +
                        "campaign.name, " +
                        "campaign.manual_cpm, " +
                        "campaign.manual_cpv, " +
                        "campaign.manual_cpa, " +
                        "campaign.id, " +
                        "campaign.advertising_channel_type, " +
                        "ad_group_ad.status, " +
                        "ad_group_ad.ad.name, " +
                        "ad_group_ad.ad.resource_name, " +
                        "ad_group.resource_name, " +
                        "campaign.resource_name, " +
                        "ad_group_ad.resource_name, " +
                        "metrics.all_conversions, " +
                        "metrics.average_cpc, " +
                        "metrics.average_cpe, " +
                        "metrics.average_cpm, " +
                        "metrics.average_cpv, " +
                        "metrics.clicks, " +
                        "metrics.conversions, " +
                        "metrics.video_views, " +
                        "metrics.video_view_rate, " +
                        "customer.id, " +
                        "customer.status, " +
                        "customer.test_account " +
                        "FROM video "
//                        "WHERE " +
//                        "campaign.id = $campaignId"
                        )

                val request = SearchGoogleAdsStreamRequest.newBuilder()
                    .setCustomerId(customerId.toString())
                    .setQuery(query)
                    .build()
                val stream = googleAdsServiceClient.searchStreamCallable().call(request)
                stream.map { response ->
                    response.resultsList.forEach { row ->
                        log.info("result row: $row")
                    }
                }
            }
        } catch (ex: IOException) {
            log.error("failed to fetch campaigns. ", ex)
        } catch (ex: StatusRuntimeException) {
            log.error("restricted to fetch campaigns.", ex)
        }
    }
}
