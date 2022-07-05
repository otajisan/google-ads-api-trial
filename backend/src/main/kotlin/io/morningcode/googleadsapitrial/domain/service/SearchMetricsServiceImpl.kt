package io.morningcode.googleadsapitrial.domain.service

import io.morningcode.googleadsapitrial.infrastructure.repository.GoogleAdsApiRepository
import org.springframework.stereotype.Service

@Service
class SearchMetricsServiceImpl(private val googleAdsApiRepository: GoogleAdsApiRepository) : SearchMetricsService {
    override fun byCampaignId(loginCustomerId: Long, customerId: Long, campaignId: Long) =
        googleAdsApiRepository.searchCampaignReport(loginCustomerId, customerId, campaignId)
}
