package com.example.data.datasource.remote.model.response.coupon

import kotlinx.serialization.Serializable

@Serializable
data class AvailableTime(
    val end: String,
    val start: String,
)
