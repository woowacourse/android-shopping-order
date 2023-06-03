package com.example.domain.repository

import com.example.domain.model.Order
import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderProduct

interface OrderRepository {
    fun getOrders(
        page: Int,
        onSuccess: (List<Order>) -> Unit,
        onFailure: () -> Unit
    )

    fun placeOrder(
        usedPoint: Int,
        orderProducts: List<OrderProduct>,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )

    fun getOrderDetail(
        orderId: Int,
        onSuccess: (OrderDetail) -> Unit,
        onFailure: () -> Unit
    )
}
