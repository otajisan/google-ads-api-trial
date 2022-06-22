package io.morningcode.googleadsapitrial.domain.model

import io.morningcode.googleadsapitrial.application.output.GetAccessibleCustomersOutputData
import java.io.Serializable

data class Customer(
    val customerId: CustomerId
) {
  data class CustomerId(
      val value: String
  ) : Serializable

  fun toCustomerDto(): GetAccessibleCustomersOutputData.CustomerDto =
      GetAccessibleCustomersOutputData.CustomerDto(id = this.customerId.value)
}

