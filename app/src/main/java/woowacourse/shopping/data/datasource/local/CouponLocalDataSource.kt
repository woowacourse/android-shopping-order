package woowacourse.shopping.data.datasource.local

import woowacourse.shopping.domain.model.coupon.Coupon

interface CouponLocalDataSource {
    suspend fun saveCoupons(coupons: List<Coupon>)

    suspend fun getCoupons(): List<Coupon>

    suspend fun getCouponById(id: Long): Coupon?
}
