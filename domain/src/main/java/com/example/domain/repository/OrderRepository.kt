package com.example.domain.repository

interface OrderRepository {
    suspend fun createOrder(cartItemIds: List<Int>)
}
