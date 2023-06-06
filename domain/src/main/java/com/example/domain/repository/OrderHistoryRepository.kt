package com.example.domain.repository

import com.example.domain.model.Order

interface OrderHistoryRepository {

    fun getOrderHistory(callback: (List<Order>) -> Unit)
}
