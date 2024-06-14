package com.example.data.datasource.remote.service

import com.example.data.datasource.remote.model.request.CreateOrderRequest
import com.example.domain.datasource.DataResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderService {
    @POST("/orders")
    suspend fun requestCreateOrder(
        @Body createOrderRequest: CreateOrderRequest,
    ): DataResponse<Unit>
}
