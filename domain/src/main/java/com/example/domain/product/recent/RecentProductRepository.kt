package com.example.domain.product.recent

import com.example.domain.product.Product
import java.time.LocalDateTime

interface RecentProductRepository {
    fun getAll(): List<RecentProduct>
    fun getRecentProduct(productId: Int): RecentProduct?
    fun getMostRecentProduct(): RecentProduct?
    fun addRecentProduct(product: Product, viewedDateTime: LocalDateTime)
}
