package io.morningcode.googleadsapitrial.domain.service

interface AdGroupService {
    fun asList(loginCustomerId: Long, customerId: Long)
    fun save(loginCustomerId: Long, customerId: Long, campaignId: Long)
    fun addPlacements(
        loginCustomerId: Long?,
        customerId: Long,
        campaignId: Long,
        adGroupId: Long,
        videoId: List<String>
    )
}
