package com.example.domain.order

data class OrderSummary(
    val id: Long,
    val finalPrice: Long,
    val products: List<String>,
    val orderDate: String
)
