package com.example.data.datasource.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val cartItemIds: List<Int>,
)
