package com.example.data.datasource.remote

import com.example.data.datasource.remote.retrofit.model.request.CreateOrderRequest
import com.example.data.datasource.remote.retrofit.service.OrderService
import com.example.domain.datasource.OrderDataSource

class RemoteOrderDataSource(
    private val service: OrderService,
) : OrderDataSource {
    override fun createOrder(cartItemIds: List<Int>) {
        val request = CreateOrderRequest(cartItemIds)
        service.requestCreateOrder(createOrderRequest = request)
    }
}
