package com.example.domain.repository

interface OrderRepository {
    fun createOrder(cartItemIds: List<Int>)
}
