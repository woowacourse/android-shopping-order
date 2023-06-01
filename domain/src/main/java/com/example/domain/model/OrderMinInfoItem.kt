package com.example.domain.model

import java.time.LocalDateTime

data class OrderMinInfoItem(
    val id: Long,
    val mainProductName: String,
    val mainProductImage: String,
    val extraProductCount: Int,
    val date: LocalDateTime,
    val price: Price
)
