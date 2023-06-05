package com.example.domain.model

data class Receipt(
    //  val orderProducts: List<OrderProduct>,
    val originPrice: Int,
    val couponName: String?,
    val totalPrice: Int,
)
