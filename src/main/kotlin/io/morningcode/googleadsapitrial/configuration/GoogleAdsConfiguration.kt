package io.morningcode.googleadsapitrial.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "api.googleads")
class GoogleAdsConfiguration {
  lateinit var clientId: String
  lateinit var clientSecret: String
  lateinit var refreshToken: String
  lateinit var developerToken: String
}