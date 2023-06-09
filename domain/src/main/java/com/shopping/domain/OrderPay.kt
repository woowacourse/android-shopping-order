package com.shopping.domain

data class OrderPay(
    val cartItemIds: List<CartItemId>,
    val originalPrice: Int,
    val points: Int
)
