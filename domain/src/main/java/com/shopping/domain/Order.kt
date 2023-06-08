package com.shopping.domain

data class Order(
    val orderId: Long,
    val orderPrice: Int,
    val totalAmount: Int,
    val previewName: String
)
