package com.example.data.datasource.remote.retrofit.service

import com.example.data.datasource.remote.retrofit.model.request.CreateOrderRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OrderService {
    @POST("/orders")
    fun requestCreateOrder(
        @Header("accept") accept: String = "*/*",
        @Body createOrderRequest: CreateOrderRequest,
    )
}
