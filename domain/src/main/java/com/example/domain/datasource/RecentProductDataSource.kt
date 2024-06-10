package com.example.domain.datasource

import com.example.domain.model.Product
import com.example.domain.model.RecentProduct

interface RecentProductDataSource {
    fun findLastOrNull(): RecentProduct?

    fun findRecentProducts(): List<RecentProduct>

    fun save(product: Product)
}
