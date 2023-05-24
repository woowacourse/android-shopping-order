package com.example.domain.repository

import com.example.domain.model.RecentProduct

interface RecentProductRepository {
    fun getAll(): List<Long>
    fun addRecentProduct(recentProduct: RecentProduct)
    fun getMostRecentProduct(): Long?
}
