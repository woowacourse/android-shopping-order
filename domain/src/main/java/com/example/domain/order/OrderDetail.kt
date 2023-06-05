package com.example.domain.order

import com.example.domain.cart.CartProducts

data class OrderDetail(
    val id: Long,
    val usedPoint: Int,
    val savedPoint: Int,
    val orderDateTime: String,
    val products: CartProducts,
)
