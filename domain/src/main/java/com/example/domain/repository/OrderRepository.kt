package com.example.domain.repository

import com.example.domain.model.Order
import com.example.domain.model.OrderDetail

interface OrderRepository {
    fun getAllOrders(onSuccess: (List<Order>) -> Unit, onFailure: () -> Unit)
    fun getOrderDetailById(orderId: Long, onSuccess: (OrderDetail) -> Unit, onFailure: () -> Unit)
    fun addOrder(
        cartIds: List<Long>,
        orderPaymentPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: () -> Unit
    )
}

// LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME);
