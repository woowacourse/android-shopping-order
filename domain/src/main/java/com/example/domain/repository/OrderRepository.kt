package com.example.domain.repository

import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderMinInfoItem

interface OrderRepository {
    fun fetchAllOrders(onSuccess: (List<OrderMinInfoItem>) -> Unit, onFailure: () -> Unit)
    fun fetchOrderDetailById(orderId: Long, onSuccess: (OrderDetail) -> Unit, onFailure: () -> Unit)
    fun addOrder(
        cartIds: List<Long>,
        orderPaymentPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: () -> Unit
    )
}
