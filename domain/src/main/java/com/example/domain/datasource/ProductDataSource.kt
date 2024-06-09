package com.example.domain.datasource

import com.example.domain.model.PagingProduct
import com.example.domain.model.Product

interface ProductDataSource {
    suspend fun find(id: Int): DataResponse<Product>

    suspend fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<Product>>

    suspend fun findPage(
        page: Int,
        size: Int,
    ): DataResponse<PagingProduct>
}
