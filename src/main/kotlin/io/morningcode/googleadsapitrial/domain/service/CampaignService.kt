package io.morningcode.googleadsapitrial.domain.service

interface CampaignService {
  fun asList(customerId: Long)
  fun asList(loginCustomerId: Long, customerId: Long)
  fun save(loginCustomerId: Long, customerId: Long)
}