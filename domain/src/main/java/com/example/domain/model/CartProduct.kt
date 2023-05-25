package com.example.domain.model

data class CartProduct(
    val cartProductId: Long,
    val product: Product,
    val count: Int,
    val isSelected: Boolean
)
