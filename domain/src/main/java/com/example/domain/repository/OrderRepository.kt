package com.example.domain.repository

import com.example.domain.model.Order

interface OrderRepository {
    fun insertOrderWithCoupon(cartItemsIds: List<Long>, couponId: Long): Result<Order>
    fun insertOrderWithoutCoupon(cartItemsIds: List<Long>): Result<Order>
}
