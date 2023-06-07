package com.example.domain.repository

import com.example.domain.model.BaseResponse
import com.example.domain.model.Product
import com.example.domain.model.RecentProduct

interface RecentProductRepository {
    fun fetchAllRecentProduct(callBack: (BaseResponse<List<RecentProduct>>) -> Unit)

    fun addRecentProduct(
        product: Product,
        callBack: (BaseResponse<Product>) -> Unit
    )
}
