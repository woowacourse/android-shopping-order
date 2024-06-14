package com.example.data.datasource.remote.service

import com.example.data.datasource.remote.model.response.coupon.CouponResponse
import com.example.domain.datasource.DataResponse
import retrofit2.http.GET

interface CouponService {
    @GET("/coupons")
    suspend fun requestCoupons(): DataResponse<List<CouponResponse>>
}
