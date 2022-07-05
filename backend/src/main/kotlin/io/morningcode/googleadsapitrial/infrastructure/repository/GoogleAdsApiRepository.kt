package io.morningcode.googleadsapitrial.infrastructure.repository

import io.morningcode.googleadsapitrial.domain.model.Customer

interface GoogleAdsApiRepository {
  fun getAccessibleCustomers(): List<Customer>?
  fun getAccountHierarchy(loginCustomerId: Long, managerId: Long)
  fun getCampaigns(customerId: Long)
  fun getCampaigns(loginCustomerId: Long?, customerId: Long)
  fun getAdGroups(loginCustomerId: Long?, customerId: Long)
  fun addCampaign(loginCustomerId: Long?, customerId: Long, videoId: String)
  fun addAdGroup(loginCustomerId: Long?, customerId: Long, campaignId: Long)
  fun addVideoPlacements(loginCustomerId: Long?, customerId: Long, campaignId: Long, adGroupId: Long, videoId: List<String>)
  fun searchCampaignReport(loginCustomerId: Long?, customerId: Long, campaignId: Long)
}
