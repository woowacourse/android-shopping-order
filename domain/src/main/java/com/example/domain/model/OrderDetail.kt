package com.example.domain.model

data class OrderDetail(
    val priceBeforeDiscount: Price,
    val priceAfterDiscount: Price,
    val orderProducts: List<OrderProduct>
)
