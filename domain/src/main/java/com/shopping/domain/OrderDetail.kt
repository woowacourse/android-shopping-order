package com.shopping.domain

data class OrderDetail(
    val orderItems: List<OrderProduct>,
    val originalPrice: Int,
    val usedPoints: Int,
    val orderPrice: Int
)
