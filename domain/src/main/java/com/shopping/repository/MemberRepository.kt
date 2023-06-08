package com.shopping.repository

import com.shopping.domain.Order
import com.shopping.domain.OrderDetail
import com.shopping.domain.Point

interface MemberRepository {
    fun getOrderHistories(onSuccess: (List<Order>) -> Unit)
    fun getOrderDetail(id: Long, onSuccess: (OrderDetail) -> Unit)
    fun getPoint(onSuccess: (Point) -> Unit)
}
