package com.example.domain.model.cart

import com.example.domain.model.product.Product

data class CartProduct(
    val cartProductId: Long,
    val product: Product,
    val count: Int,
    val isSelected: Boolean
)
