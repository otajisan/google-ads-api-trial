package io.morningcode.googleadsapitrial.domain.service

import io.morningcode.googleadsapitrial.infrastructure.repository.GoogleAdsApiRepository
import org.springframework.stereotype.Service

@Service
class AccountHierarchyServiceImpl(
    private val googleAdsApiRepository: GoogleAdsApiRepository
) : AccountHierarchyService {

  override fun asList(loginCustomerId: Long) = asList(loginCustomerId, loginCustomerId)

  override fun asList(loginCustomerId: Long, managerId: Long) = googleAdsApiRepository.getAccountHierarchy(loginCustomerId, managerId)

}