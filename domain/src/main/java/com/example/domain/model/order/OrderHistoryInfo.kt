package com.example.domain.model.order

data class OrderHistoryInfo(
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val info: List<OrderHistoryProduct>
)
