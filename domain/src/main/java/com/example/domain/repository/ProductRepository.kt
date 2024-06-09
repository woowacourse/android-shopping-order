package com.example.domain.repository

import com.example.domain.datasource.DataResponse
import com.example.domain.model.PagingProduct
import com.example.domain.model.Product

interface ProductRepository {
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
