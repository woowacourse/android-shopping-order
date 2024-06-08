package com.example.domain.datasource

import com.example.domain.model.Product

interface ProductDataSource {
    fun find(id: Int): DataResponse<Product>

    fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<Product>>
}
