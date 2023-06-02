package com.example.domain.repository

import com.example.domain.model.Order
import com.example.domain.model.OrderItem

interface OrderRepository {
    fun getOrders(
        page: Int,
        onSuccess: (List<Order>) -> Unit,
        onFailure: () -> Unit
    )

    fun placeOrder(usedPoint: Int, orderItems: List<OrderItem>)
}
