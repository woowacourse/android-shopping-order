package com.example.data.repository

import com.example.data.datasource.remote.RemoteOrderDataSource
import com.example.domain.repository.OrderRepository

class DefaultOrderRepository(
    private val orderDataSource: RemoteOrderDataSource,
) : OrderRepository {
    override fun createOrder(cartItemIds: List<Int>) {
        orderDataSource.createOrder(cartItemIds)
    }
}
