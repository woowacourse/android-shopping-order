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

    fun placeOrder(usedPoint: Int, orderProducts: List<OrderProduct>)

    fun getOrderDetail(orderId: Int): OrderDetail
}
