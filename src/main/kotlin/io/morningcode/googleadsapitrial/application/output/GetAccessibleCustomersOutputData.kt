package io.morningcode.googleadsapitrial.application.output

data class GetAccessibleCustomersOutputData(
    val customers: List<CustomerDto>?
) {
  data class CustomerDto(
      val id: String
  )
}


