package com.example.domain.repository

import com.example.domain.model.Order

interface OrderRepository {
    fun getOrders(
        page: Int,
        onSuccess: (List<Order>) -> Unit,
        onFailure: () -> Unit
    )
}
