package com.example.domain.repository

import com.example.domain.model.OrderDetailProduct
import com.example.domain.model.OrderHistoryInfo
import com.example.domain.model.OrderInfo
import com.example.domain.model.Point

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
        callback: (Result<OrderInfo>) -> Unit

    )
}
