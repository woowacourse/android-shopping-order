package com.example.domain.model.order

import com.example.domain.model.Price
import com.example.domain.model.point.Point

data class Order(
    val orderId: Int,
    val orderAt: String,
    val orderState: OrderState,
    val payAmount: Price,
    val usedPoint: Point,
    val savedPoint: Point,
    val products: List<OrderDetailProduct>
)
