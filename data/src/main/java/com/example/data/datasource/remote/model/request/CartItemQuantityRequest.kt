package com.example.data.datasource.remote.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CartItemQuantityRequest(
    val quantity: Int,
)
