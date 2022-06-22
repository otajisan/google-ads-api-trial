package io.morningcode.googleadsapitrial.domain.service

import io.morningcode.googleadsapitrial.domain.model.Customer
import io.morningcode.googleadsapitrial.infrastructure.repository.GoogleAdsApiRepository
import org.springframework.stereotype.Service

@Service
class CampaignServiceImpl(private val googleAdsApiRepository: GoogleAdsApiRepository) : CampaignService {

  override fun asList(customerId: String) =
      googleAdsApiRepository.getCampaigns(customerId)
}