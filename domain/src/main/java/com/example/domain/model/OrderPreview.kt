package com.example.domain.model

data class OrderPreview(
    val orderId: Long,
    val mainName: String,
    val mainImageUrl: String,
    val extraProductCount: Int,
    val date: String,
    val paymentAmount: Int,
)
