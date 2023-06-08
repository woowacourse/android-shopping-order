package com.example.domain.order

import com.example.domain.product.Product

data class OrderProduct(
    val id: Long,
    val quantity: Int,
    val product: Product
)
