package woowacourse.shopping.data.payment.datasource

import woowacourse.shopping.remote.service.CouponService

object CouponDataSourceInjector {
    private var instance: CouponDataSource? = null

    fun couponDataSource(): CouponDataSource =
        instance ?: synchronized(this) {
            instance ?: CouponDataSourceImpl(
                CouponService.instance(),
            )
        }.also { instance = it }
}
