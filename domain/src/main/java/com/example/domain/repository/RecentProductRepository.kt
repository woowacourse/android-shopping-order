package com.example.domain.repository

import com.example.domain.model.RecentProduct

interface RecentProductRepository {
    fun getAll(
        onSuccess: (List<RecentProduct>) -> Unit,
        onFailure: () -> Unit,
    )

    fun addRecentProduct(recentProduct: RecentProduct)
}
