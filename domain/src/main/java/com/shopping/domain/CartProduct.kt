package com.shopping.domain

data class CartProduct(
    val id: Long,
    val product: Product,
    val count: Count,
    val isSelected: Boolean
)
