package io.morningcode.googleadsapitrial.infrastructure.repository

import io.morningcode.googleadsapitrial.domain.model.Customer

interface GoogleAdsApiRepository {
  fun getAccessibleCustomers(): List<Customer>?
  fun getAccountHierarchy(loginCustomerId: Long, managerId: Long)
  fun getCampaigns(customerId: Long)
  fun getCampaigns(loginCustomerId: Long?, customerId: Long)
}