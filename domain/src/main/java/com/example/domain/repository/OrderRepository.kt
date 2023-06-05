package com.example.domain.repository

import com.example.domain.model.Coupon
import com.example.domain.model.TotalPrice
import com.example.domain.util.CustomResult

interface OrderRepository {
    fun getCoupons(
        onSuccess: (List<Coupon>) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )

    fun postOrder()
    fun getAppliedPrice(
        totalPrice: Int,
        couponId: Int,
        onSuccess: (TotalPrice) -> Unit,
        onFailure: (CustomResult<Error>) -> Unit,
    )
}
