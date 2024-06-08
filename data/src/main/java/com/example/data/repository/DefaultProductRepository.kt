package com.example.data.repository

import com.example.domain.datasource.ProductDataSource

class DefaultProductRepository(
    private val defaultProductDataSource: ProductDataSource,
) {
    /*
    override fun find(id: Int): Product =
        defaultProductDataSource.find(id)

    override fun findRange(page: Int, pageSize: Int): List<Product> =
        defaultProductDataSource.findRange(page, pageSize)

     */
}
