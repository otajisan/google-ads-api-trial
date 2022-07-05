package io.morningcode.googleadsapitrial.application.input

data class CreateNewPlacementsRequest(
    val campaignId: Long,
    val adGroupId: Long,
    val videoIds: List<String>
)
