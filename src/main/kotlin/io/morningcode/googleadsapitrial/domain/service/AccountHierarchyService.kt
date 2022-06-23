package io.morningcode.googleadsapitrial.domain.service

interface AccountHierarchyService {
  fun asList(loginCustomerId: Long)
  fun asList(loginCustomerId: Long, managerId: Long)
}