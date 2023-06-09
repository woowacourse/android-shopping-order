package com.example.domain.model

data class OrderProduct(
    val productId: Long,
    val name: String,
    val price: Int,
    val count: Int,
    val imageUrl: String,
)
