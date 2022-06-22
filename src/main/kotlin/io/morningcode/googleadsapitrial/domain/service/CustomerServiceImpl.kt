package io.morningcode.googleadsapitrial.domain.service

import io.morningcode.googleadsapitrial.application.output.GetAccessibleCustomersOutputData
import io.morningcode.googleadsapitrial.infrastructure.repository.GoogleAdsApiRepository
import org.springframework.stereotype.Service

@Service
class CustomerServiceImpl(private val googleAdsApiRepository: GoogleAdsApiRepository) : CustomerService {

  override fun asList(): GetAccessibleCustomersOutputData =
      GetAccessibleCustomersOutputData(customers = googleAdsApiRepository.getAccessibleCustomers()?.map {
        it.toCustomerDto()
      })
}