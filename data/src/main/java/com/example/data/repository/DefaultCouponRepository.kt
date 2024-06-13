package com.example.data.repository

import com.example.domain.datasource.CouponDataSource
import com.example.domain.datasource.DataResponse
import com.example.domain.model.coupon.Coupon
import com.example.domain.repository.CouponRepository

class DefaultCouponRepository(
    private val dataSource: CouponDataSource,
) : CouponRepository {
    override suspend fun getCoupons(): DataResponse<List<Coupon>> = dataSource.getCoupons()
}
