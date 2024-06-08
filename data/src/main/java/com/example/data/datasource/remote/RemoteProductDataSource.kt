package com.example.data.datasource.remote

import com.example.data.datasource.remote.retrofit.model.response.product.toProduct
import com.example.data.datasource.remote.retrofit.model.response.product.toProductList
import com.example.data.datasource.remote.retrofit.service.ProductService
import com.example.domain.datasource.DataResponse
import com.example.domain.datasource.ProductDataSource
import com.example.domain.datasource.map
import com.example.domain.model.Product

class RemoteProductDataSource(
    private val productService: ProductService,
) : ProductDataSource {
    override fun find(id: Int): DataResponse<Product> =
        productService.requestProduct().execute().body()?.map { it.toProduct() }
            ?: DataResponse.Failure(DataResponse.NULL_BODY_ERROR_CODE, DataResponse.NULL_BODY_ERROR_STRING)

    override fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<Product>> =
        productService.requestProducts(page = page, size = pageSize).execute().body()?.map { it.toProductList() }
            ?: DataResponse.Failure(DataResponse.NULL_BODY_ERROR_CODE, DataResponse.NULL_BODY_ERROR_STRING)
}
