package com.example.domain.model

data class OrderDetail(
    val priceBeforeDiscount: Int,
    val priceAfterDiscount: Int,
    val date: String,
    val orderItems: List<OrderProduct>,
)
