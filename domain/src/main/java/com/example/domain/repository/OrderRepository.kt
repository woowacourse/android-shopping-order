package com.example.domain.repository

import com.example.domain.model.BaseResponse
import com.example.domain.model.OrderDetail
import com.example.domain.model.OrderMinInfoItem

interface OrderRepository {
    fun fetchAllOrders(callBack: (BaseResponse<List<OrderMinInfoItem>>) -> Unit)
    fun fetchOrderDetailById(orderId: Long, callBack: (BaseResponse<OrderDetail>) -> Unit)
    fun addOrder(
        cartIds: List<Long>,
        orderPaymentPrice: Int,
        callBack: (orderId: BaseResponse<Long>) -> Unit
    )
}
