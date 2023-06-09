package com.example.domain.repository

import com.example.domain.model.Order

interface OrderRepository {
    fun insertOrderWithCoupon(cartItemsIds: List<Long>, couponId: Long, callback: (Order) -> Unit)
    fun insertOrderWithoutCoupon(cartItemsIds: List<Long>, callback: (Order) -> Unit)
}
