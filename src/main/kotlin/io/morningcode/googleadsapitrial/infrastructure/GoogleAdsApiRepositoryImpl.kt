package io.morningcode.googleadsapitrial.infrastructure

import com.google.ads.googleads.lib.GoogleAdsClient
import com.google.ads.googleads.v11.services.ListAccessibleCustomersRequest
import com.google.ads.googleads.v11.services.SearchGoogleAdsStreamRequest
import com.google.auth.oauth2.UserCredentials
import io.grpc.StatusRuntimeException
import io.morningcode.googleadsapitrial.configuration.GoogleAdsConfiguration
import io.morningcode.googleadsapitrial.exception.ApiUnexpectedException
import io.morningcode.googleadsapitrial.util.logger
import org.springframework.stereotype.Repository
import java.io.FileNotFoundException
import java.io.IOException
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

  private fun buildClient() =
      buildUserCredentials().let {
        googleAdsClient.toBuilder().setCredentials(it).build()
      }

  override fun getAccessibleCustomers() {
    val client = buildClient()
    try {
      client.latestVersion.createCustomerServiceClient().use { customerService ->
        val response = customerService.listAccessibleCustomers(ListAccessibleCustomersRequest.newBuilder().build())
        response.resourceNamesList.map { customerResourceName ->
          log.info("accessible customer name: $customerResourceName")
        }
      }
    } catch (ex: IOException) {
      log.error("failed to fetch accessible customers. ", ex)
    } catch (ex: StatusRuntimeException) {
      log.error("restricted to fetch customers.", ex)
    }
  }

  override fun getCampaigns(customerId: String) {
    val client = buildClient()
    try {
      client.latestVersion.createGoogleAdsServiceClient().use { googleAdsServiceClient ->
        val query = "SELECT campaign.id, campaign.name FROM campaign ORDER BY campaign.id"
        val request = SearchGoogleAdsStreamRequest.newBuilder()
            .setCustomerId(customerId)
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
}