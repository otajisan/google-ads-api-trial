package io.morningcode.googleadsapitrial.domain

import io.morningcode.googleadsapitrial.infrastructure.GoogleAdsApiRepository
import org.springframework.stereotype.Service

@Service
class CustomersImpl(private val googleAdsApiRepository: GoogleAdsApiRepository): Customers {

  override fun asList() {
    googleAdsApiRepository.getAccessibleCustomers()
  }
}