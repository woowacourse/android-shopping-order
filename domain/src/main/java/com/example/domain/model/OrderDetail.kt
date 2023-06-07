package com.example.domain.model

import java.time.LocalDateTime

data class OrderDetail(
    val priceBeforeDiscount: Price,
    val priceAfterDiscount: Price,
    val dateTime: LocalDateTime,
    val orderItems: List<OrderProduct>
)
