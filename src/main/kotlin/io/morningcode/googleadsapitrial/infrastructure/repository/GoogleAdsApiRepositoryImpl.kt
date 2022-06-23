package io.morningcode.googleadsapitrial.infrastructure.repository

import com.google.ads.googleads.lib.GoogleAdsClient
import com.google.ads.googleads.v11.common.AdVideoAsset
import com.google.ads.googleads.v11.resources.Campaign
import com.google.ads.googleads.v11.resources.CampaignBudget
import com.google.ads.googleads.v11.resources.CustomerClient
import com.google.ads.googleads.v11.services.*
import com.google.ads.googleads.v11.common.ManualCpc
import com.google.ads.googleads.v11.common.YoutubeVideoAsset
import com.google.ads.googleads.v11.enums.*
import com.google.ads.googleads.v11.resources.Asset
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
        val response = customerService.listAccessibleCustomers(ListAccessibleCustomersRequest.newBuilder().build())
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

            if (customerClient.id!=searchCustomerId &&
                customerClient.id!=managerId) {
              // put result
              resultHierarchy.put(searchCustomerId, customerClient)
              // add next entry
              if (customerClient.manager && customerClient.level==1L) {
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

  override fun addCampaign(loginCustomerId: Long?, customerId: Long) {
    val client = buildClient(loginCustomerId)

    // TODO:
    val videoId = ""

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
        val response = campaignBudgetServiceClient.mutateCampaignBudgets(customerId.toString(), listOf(operation))
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
}