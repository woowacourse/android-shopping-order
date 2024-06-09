package com.example.data.datasource.remote.service

import com.example.data.datasource.remote.model.request.CreateOrderRequest
import com.example.domain.datasource.DataResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OrderService {
    @POST("/orders")
    fun requestCreateOrder(
        @Header("accept") accept: String = "*/*",
        @Body createOrderRequest: CreateOrderRequest,
    ): Call<DataResponse<Unit>>
}
