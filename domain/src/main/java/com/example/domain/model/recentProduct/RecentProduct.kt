package com.example.domain.model.recentProduct

import com.example.domain.model.product.Product
import java.time.LocalDateTime

data class RecentProduct(
    val product: Product,
    val dateTime: LocalDateTime
)
