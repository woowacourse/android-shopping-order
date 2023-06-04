package com.example.domain.repository

import com.example.domain.model.Order

interface OrderDetailRepository {
    fun getById(id: Long): Result<Order>
}
