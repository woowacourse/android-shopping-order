package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupon.Coupon

interface CouponRepository {
    suspend fun getCoupons(): Result<List<Coupon>>

    companion object {
        private var instance: CouponRepository? = null

        fun setInstance(couponRepository: CouponRepository) {
            instance = couponRepository
        }

        fun getInstance(): CouponRepository = requireNotNull(instance)
    }
}
