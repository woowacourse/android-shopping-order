package woowacourse.shopping.data.remote.injector

import woowacourse.shopping.data.remote.datasource.coupon.RetrofitCouponDataSource
import woowacourse.shopping.data.remote.repository.CouponRepositoryImpl
import woowacourse.shopping.domain.repository.CouponRepository

object CouponRepositoryInjector {
    var instance: CouponRepository =
        CouponRepositoryImpl(
            RetrofitCouponDataSource(),
        )
        private set

    fun setInstance(couponRepository: CouponRepository) {
        instance = couponRepository
    }
}
