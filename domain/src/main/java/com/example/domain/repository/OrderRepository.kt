package com.example.domain.repository

import com.example.domain.model.order.Order
import com.example.domain.model.order.OrderDetailProduct
import com.example.domain.model.order.OrderHistoryInfo
import com.example.domain.model.point.Point

interface OrderRepository {

    fun addOrder(
        usedPoint: Point,
        orderDetailProduct: List<OrderDetailProduct>,
        callback: (Result<Unit>) -> Unit
    )

    fun cancelOrder(
        orderId: Int,
        callback: (Result<Unit>) -> Unit
    )

    fun getOrderHistory(
        currentPageNum: Int,
        callback: (Result<OrderHistoryInfo>) -> Unit
    )

    fun getOrderDetail(
        orderId: Int,
        callback: (Result<Order>) -> Unit

    )
}
