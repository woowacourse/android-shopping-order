package com.example.domain.repository

import com.example.domain.model.Coupon
import com.example.domain.model.CouponDiscountPrice

interface CouponRepository {
    fun getCoupons(): Result<List<Coupon>>
    fun getPriceWithCoupon(originalPrice: Int, couponId: Long): Result<CouponDiscountPrice>
}
