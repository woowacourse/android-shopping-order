package com.example.data.datasource.remote.service

import com.example.data.datasource.remote.model.response.product.ProductContent
import com.example.data.datasource.remote.model.response.product.ProductResponse
import com.example.domain.datasource.DataResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @GET("/products")
    suspend fun requestProducts(
        @Query("page") page: Int,
        @Query("size") size: Int = 20,
    ): DataResponse<ProductResponse>

    @GET("/products/{id}")
    suspend fun requestProduct(
        @Path("id") id: Int,
    ): DataResponse<ProductContent>
}
