package io.morningcode.googleadsapitrial.infrastructure

interface GoogleAdsApiRepository {
  fun getAccessibleCustomers()
  fun getCampaigns(customerId: String)
}