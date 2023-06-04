package com.example.domain.model

data class Order(
    val id: Long,
    val orderProducts: List<OrderProduct>,
    val timestamp: String,
    val couponName: String?,
    val originPrice: Int,
    val discountPrice: Int,
    val totalPrice: Int,

)
