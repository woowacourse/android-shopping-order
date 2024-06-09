package com.example.data.repository

import com.example.domain.datasource.DataResponse
import com.example.domain.datasource.ProductDataSource
import com.example.domain.model.PagingProduct
import com.example.domain.model.Product
import com.example.domain.repository.ProductRepository

class DefaultProductRepository(
    private val defaultProductDataSource: ProductDataSource,
) : ProductRepository {
    override suspend fun find(id: Int): DataResponse<Product> = defaultProductDataSource.find(id)

    override suspend fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<Product>> = defaultProductDataSource.findRange(page, pageSize)

    override suspend fun findPage(
        page: Int,
        size: Int,
    ): DataResponse<PagingProduct> = defaultProductDataSource.findPage(page, size)
}
