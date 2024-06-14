package com.example.domain.datasource

interface OrderDataSource {
    suspend fun createOrder(cartItemIds: List<Int>)
}
