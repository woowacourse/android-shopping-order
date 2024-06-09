package com.example.data.datasource.remote

import com.example.data.datasource.remote.model.response.coupon.toCoupon
import com.example.data.datasource.remote.service.CouponService
import com.example.domain.datasource.CouponDataSource
import com.example.domain.datasource.DataResponse
import com.example.domain.datasource.map
import com.example.domain.model.Coupon

class RemoteCouponDataSource(
    private val couponService: CouponService,
) : CouponDataSource {
    override suspend fun getCoupons(): DataResponse<List<Coupon>> =
        couponService.requestCoupons().map { couponResponses ->
            couponResponses.map {
                it.toCoupon()
            }
        }
}
