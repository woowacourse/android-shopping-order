package com.example.data.datasource.remote.model.response.count

import kotlinx.serialization.Serializable

@Serializable
data class CountResponse(
    val quantity: Int,
)
