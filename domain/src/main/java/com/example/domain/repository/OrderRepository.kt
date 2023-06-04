package com.example.domain.repository

import com.example.domain.model.Order
import com.example.domain.model.OrderDetail

interface OrderRepository {

    fun addOrder(
        cartIds: List<Long>,
        totalPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: () -> Unit,
    )

    fun getAll(
        onSuccess: (orders: List<Order>) -> Unit,
        onFailure: () -> Unit,
    )

    fun getOrderDetail(
        orderId: Long,
        onSuccess: (orderDetail: OrderDetail) -> Unit,
        onFailure: () -> Unit,
    )
}
