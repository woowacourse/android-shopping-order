package com.example.domain.repository

import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderPreview

interface OrderRepository {

    fun addOrder(
        cartIds: List<Long>,
        totalPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: () -> Unit,
    )

    fun getAll(
        onSuccess: (orders: List<OrderPreview>) -> Unit,
        onFailure: () -> Unit,
    )

    fun getOrderDetail(
        orderId: Long,
        onSuccess: (orderDetail: OrderDetail) -> Unit,
        onFailure: () -> Unit,
    )
}
