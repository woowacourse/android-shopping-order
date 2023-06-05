package com.example.domain.repository

import com.example.domain.model.Coupon
import com.example.domain.model.OrderNumber
import com.example.domain.model.Receipt
import com.example.domain.model.TotalPrice
import com.example.domain.util.CustomResult

interface OrderRepository {
    fun getCoupons(
        onSuccess: (List<Coupon>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun postOrderWithCoupon(
        cartItemIds: List<Int>,
        couponId: Int,
        onSuccess: (OrderNumber) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun postOrderWithoutCoupon(
        cartItemIds: List<Int>,
        onSuccess: (OrderNumber) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun getAppliedPrice(
        totalPrice: Int,
        couponId: Int,
        onSuccess: (TotalPrice) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun getReceipt(
        orderId: Int,
        onSuccess: (Receipt) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )
}
