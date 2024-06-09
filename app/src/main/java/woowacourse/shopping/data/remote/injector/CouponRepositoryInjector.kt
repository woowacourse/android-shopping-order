package woowacourse.shopping.data.remote.injector

import woowacourse.shopping.data.remote.datasource.coupon.DefaultCouponDataSource
import woowacourse.shopping.data.remote.repository.CouponRepositoryImpl
import woowacourse.shopping.domain.CouponRepository

object CouponRepositoryInjector {
    var instance: CouponRepository =
        CouponRepositoryImpl(
            DefaultCouponDataSource(),
        )
        private set

    fun setInstance(couponRepository: CouponRepository) {
        instance = couponRepository
    }
}
