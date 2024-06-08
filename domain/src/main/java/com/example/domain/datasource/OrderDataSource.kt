package com.example.domain.datasource

interface OrderDataSource {
    fun createOrder(cartItemIds: List<Int>)
}
