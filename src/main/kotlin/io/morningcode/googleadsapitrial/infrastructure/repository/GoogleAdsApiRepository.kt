package io.morningcode.googleadsapitrial.infrastructure.repository

import io.morningcode.googleadsapitrial.domain.model.Customer

interface GoogleAdsApiRepository {
  fun getAccessibleCustomers(): List<Customer>?
  fun getCampaigns(customerId: String)
}