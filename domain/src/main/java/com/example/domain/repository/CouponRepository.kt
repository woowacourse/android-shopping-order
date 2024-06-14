package com.example.domain.repository

import com.example.domain.datasource.DataResponse
import com.example.domain.model.coupon.Coupon

interface CouponRepository {
    suspend fun getCoupons(): DataResponse<List<Coupon>>
}
