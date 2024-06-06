package woowacourse.shopping.domain.repository

import woowacourse.shopping.domain.model.coupon.CouponState

interface CouponRepository {
    suspend fun getCoupons(): Result<List<CouponState>>

    companion object {
        private var instance: CouponRepository? = null

        fun setInstance(couponRepository: CouponRepository) {
            instance = couponRepository
        }

        fun getInstance(): CouponRepository = requireNotNull(instance)
    }
}
