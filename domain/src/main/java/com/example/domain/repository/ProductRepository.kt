package com.example.domain.repository

import com.example.domain.datasource.DataResponse
import com.example.domain.model.Product

interface ProductRepository {
    fun find(id: Int): DataResponse<Product>

    fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<Product>>
}
