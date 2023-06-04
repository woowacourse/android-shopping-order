package com.example.domain.model

data class OrderHistoryInfo(
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int,
    val info: List<OrderHistoryProduct>
)
