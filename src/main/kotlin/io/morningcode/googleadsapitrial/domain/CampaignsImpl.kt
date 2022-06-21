package io.morningcode.googleadsapitrial.domain

import io.morningcode.googleadsapitrial.infrastructure.GoogleAdsApiRepository
import org.springframework.stereotype.Service

@Service
class CampaignsImpl(private val googleAdsApiRepository: GoogleAdsApiRepository) : Campaigns {

  override fun asList(customerId: String) {
    googleAdsApiRepository.getCampaigns(customerId)
  }
}