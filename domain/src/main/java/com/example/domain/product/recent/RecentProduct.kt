package com.example.domain.product.recent

import java.time.LocalDateTime

data class RecentProduct(
    val productId: Long,
    val productImageUrl: String,
    val productName: String,
    val productPrice: Int,
    val viewedDateTime: LocalDateTime
)
