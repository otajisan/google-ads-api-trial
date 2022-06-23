package io.morningcode.googleadsapitrial.domain.service

import io.morningcode.googleadsapitrial.infrastructure.repository.GoogleAdsApiRepository
import org.springframework.stereotype.Service

@Service
class AccountHierarchyServiceImpl(
    private val googleAdsApiRepository: GoogleAdsApiRepository
): AccountHierarchyService {

  override fun asList(seedCustomerId: Long) {
    googleAdsApiRepository.getAccountHierarchy(seedCustomerId)
  }
}