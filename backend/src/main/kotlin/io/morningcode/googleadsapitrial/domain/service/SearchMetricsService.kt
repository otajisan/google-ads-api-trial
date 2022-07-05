package io.morningcode.googleadsapitrial.domain.service

interface SearchMetricsService {
    fun byCampaignId(loginCustomerId: Long, customerId: Long, campaignId: Long)
}
