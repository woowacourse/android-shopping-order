package com.example.data.datasource.remote

import com.example.data.datasource.remote.model.response.product.toPagingProduct
import com.example.data.datasource.remote.model.response.product.toProduct
import com.example.data.datasource.remote.model.response.product.toProductList
import com.example.data.datasource.remote.service.ProductService
import com.example.domain.datasource.DataResponse
import com.example.domain.datasource.ProductDataSource
import com.example.domain.datasource.map
import com.example.domain.model.PagingProduct
import com.example.domain.model.Product

class RemoteProductDataSource(
    private val productService: ProductService,
) : ProductDataSource {
    override fun find(id: Int): DataResponse<Product> =
        productService.requestProduct(id = id).executeForDataResponse().map { it.toProduct() }

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<Product>> =
        productService.requestProducts(page = page, size = pageSize).executeForDataResponse()
            .map { it.toProductList() }

    override fun findPage(
        page: Int,
        size: Int,
    ): DataResponse<PagingProduct> =
        productService.requestProducts(page = page, size = size).executeForDataResponse()
            .map { it.toPagingProduct() }
}
