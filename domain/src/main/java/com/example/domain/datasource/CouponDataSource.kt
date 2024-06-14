package com.example.domain.datasource

import com.example.domain.model.coupon.Coupon

interface CouponDataSource {
    suspend fun getCoupons(): DataResponse<List<Coupon>>
}
