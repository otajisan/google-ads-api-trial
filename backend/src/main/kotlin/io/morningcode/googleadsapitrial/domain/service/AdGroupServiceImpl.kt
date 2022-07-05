package io.morningcode.googleadsapitrial.domain.service

import io.morningcode.googleadsapitrial.infrastructure.repository.GoogleAdsApiRepository
import org.springframework.stereotype.Service

@Service
class AdGroupServiceImpl(private val googleAdsApiRepository: GoogleAdsApiRepository) : AdGroupService {

    override fun asList(loginCustomerId: Long, customerId: Long)=
        googleAdsApiRepository.getAdGroups(loginCustomerId, customerId)

    override fun save(loginCustomerId: Long, customerId: Long, campaignId: Long) =
        googleAdsApiRepository.addAdGroup(loginCustomerId, customerId, campaignId)

    override fun addPlacements(
        loginCustomerId: Long?,
        customerId: Long,
        campaignId: Long,
        adGroupId: Long,
        videoId: List<String>
    ) = googleAdsApiRepository.addVideoPlacements(loginCustomerId, customerId, campaignId, adGroupId, videoId)
}
