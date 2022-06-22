package io.morningcode.googleadsapitrial.domain.service

import io.morningcode.googleadsapitrial.application.output.GetAccessibleCustomersOutputData

interface CustomerService {
  fun asList(): GetAccessibleCustomersOutputData
}