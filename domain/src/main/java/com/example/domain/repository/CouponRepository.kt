package com.example.domain.repository

import com.example.domain.model.Coupon
import com.example.domain.model.CouponDiscountPrice

interface CouponRepository {
    fun getCoupons(callback: (List<Coupon>) -> Unit)
    fun getPriceWithCoupon(originalPrice: Int, couponId: Long, callback: (CouponDiscountPrice) -> Unit)
}
