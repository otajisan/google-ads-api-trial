package io.morningcode.googleadsapitrial.domain.service

import io.morningcode.googleadsapitrial.domain.model.Customer
import io.morningcode.googleadsapitrial.infrastructure.repository.GoogleAdsApiRepository
import org.springframework.stereotype.Service

@Service
class CampaignServiceImpl(private val googleAdsApiRepository: GoogleAdsApiRepository) : CampaignService {

  override fun asList(customerId: Long) =
      googleAdsApiRepository.getCampaigns(customerId)

  override fun asList(loginCustomerId: Long, customerId: Long) =
      googleAdsApiRepository.getCampaigns(loginCustomerId, customerId)
}