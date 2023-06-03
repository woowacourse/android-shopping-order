package com.example.domain.model

data class OrderInfo(
    val orderId: Int,
    val orderAt: String,
    val orderState: OrderState,
    val payAmount: Price,
    val usedPoint: Point,
    val savedPoint: Point,
    val products: List<OrderDetailProduct>
)
