package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.remote.api.CouponService
import woowacourse.shopping.data.source.remote.api.safeNetworkApiCall

class DefaultOrderRepository(
    val remoteCouponDataSource: CouponService,
) : OrderRepository {
    override suspend fun getCoupons() =
        safeNetworkApiCall {
            remoteCouponDataSource.getCoupons()
        }
}
