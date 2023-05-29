package com.example.domain.model

data class Order(
    val orderId: Int,
    val payAmount: Int,
    val orderAt: String,
    val productName: String,
    val productImageUrl: String,
    val totalProductCount: Int
)
