package com.example.data.datasource.local

import com.example.data.datasource.local.dao.RecentProductDao
import com.example.domain.datasource.RecentProductDataSource
import com.example.domain.model.Product
import com.example.domain.model.RecentProduct

class LocalRecentDataSource(private val recentProductDao: RecentProductDao) :
    RecentProductDataSource {
    override fun findLastOrNull(): RecentProduct? {
        TODO("Not yet implemented")
    }

    override fun findRecentProducts(): List<RecentProduct> {
        TODO("Not yet implemented")
    }

    override fun save(product: Product) {
        TODO("Not yet implemented")
    }
}
