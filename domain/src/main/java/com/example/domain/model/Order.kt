package com.example.domain.model

data class Order(
    val orderId: Int,
    val payAmount: Price,
    val orderAt: String,
    val orderStatus: OrderStatus,
    val productName: String,
    val productImageUrl: String,
    val totalProductCount: Int
)
