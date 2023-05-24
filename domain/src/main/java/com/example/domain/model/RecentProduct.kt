package com.example.domain.model

import java.time.LocalDateTime

data class RecentProduct(
    val productId: Long,
    val imageUrl: String,
    val dateTime: LocalDateTime,
)
