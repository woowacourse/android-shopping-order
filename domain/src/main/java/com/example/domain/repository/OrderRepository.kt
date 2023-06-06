package com.example.domain.repository

import com.example.domain.FixedDiscountPolicy
import com.example.domain.order.Order
import com.example.domain.order.OrderSummary

interface OrderRepository {

    fun requestFetchAllOrders(
        onSuccess: (List<OrderSummary>) -> Unit,
        onFailure: () -> Unit
    )

    fun requestAddOrder(
        cartIds: List<Long>,
        finalPrice: Int,
        onSuccess: (orderId: Long) -> Unit,
        onFailure: () -> Unit
    )

    fun requestFetchOrderById(
        id: Long,
        onSuccess: (order: Order?) -> Unit,
        onFailure: () -> Unit
    )

    fun requestFetchDiscountPolicy(
        onSuccess: (fixedDiscountPolicy: FixedDiscountPolicy) -> Unit,
        onFailure: () -> Unit
    )
}
