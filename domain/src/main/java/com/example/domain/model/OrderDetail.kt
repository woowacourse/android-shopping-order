package com.example.domain.model

data class OrderDetail(
    val orderId: Int,
    val orderAt: String,
    val orderStatus: OrderStatus,
    val payAmount: Int,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderProducts: List<OrderProduct>
)
