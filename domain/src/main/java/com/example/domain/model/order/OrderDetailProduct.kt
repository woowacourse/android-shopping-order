package com.example.domain.model.order

import com.example.domain.model.product.Product

data class OrderDetailProduct(
    val quantity: Int,
    val product: Product
)
