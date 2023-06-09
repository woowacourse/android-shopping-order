package com.example.domain.order

data class Order(
    val id: Long,
    val originalPrice: Int,
    val discountPrice: Int,
    val finalPrice: Int,
    val orderDate: String,
    val orderProducts: List<OrderProduct>
)
