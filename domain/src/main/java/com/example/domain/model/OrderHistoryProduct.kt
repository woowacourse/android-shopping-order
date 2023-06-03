package com.example.domain.model

data class OrderHistoryProduct(
    val orderId: Int,
    val payAmount: Price,
    val orderAt: String,
    val orderStatus: OrderState,
    val productName: String,
    val productImageUrl: String,
    val totalProductCount: Int
)
