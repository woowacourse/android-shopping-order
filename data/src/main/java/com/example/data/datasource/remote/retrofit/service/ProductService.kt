package com.example.data.datasource.remote.retrofit.service

import com.example.data.datasource.remote.retrofit.model.response.product.ProductContent
import com.example.data.datasource.remote.retrofit.model.response.product.ProductResponse
import com.example.domain.datasource.DataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @GET("/products")
    fun requestProducts(
        @Header("accept") accept: String = "*/*",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
    ): Call<DataResponse<ProductResponse>>

    @GET("/products/{id}")
    fun requestProduct(
        @Header("accept") accept: String = "*/*",
        @Path("id") id: Int = 0,
    ): Call<DataResponse<ProductContent>>
}
